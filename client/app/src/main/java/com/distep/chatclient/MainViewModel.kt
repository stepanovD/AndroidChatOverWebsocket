package com.distep.chatclient

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distep.chatclient.data.GsonLocalDateTimeAdapter
import com.distep.chatclient.data.db.AppDb
import com.distep.chatclient.data.dto.ChatSocketMessage
import com.distep.chatclient.data.dto.dtoToEntity
import com.distep.chatclient.data.dto.entityToDto
import com.distep.chatclient.data.entity.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage
import ua.naiksoftware.stomp.provider.OkHttpConnectionProvider.TAG
import java.time.LocalDateTime
import javax.inject.Inject

class MainViewModel @Inject constructor(
    var db: AppDb
) : ViewModel() {
    companion object{
        const val SOCKET_URL = "ws://10.0.2.2:8080/api/v1/chat/websocket"
        const val CHAT_TOPIC = "/topic/chat"
        const val CHAT_LINK_SOCKET = "/api/v1/chat/sock"
    }

    private val gson: Gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
        GsonLocalDateTimeAdapter()
    ).create()
    private var mStompClient: StompClient? = null
    private var compositeDisposable: CompositeDisposable? = null

    private val _chatState = MutableLiveData<Message?>()
    val liveChatState: LiveData<Message?> = _chatState

    init {
//            val headerMap: Map<String, String> =
//                Collections.singletonMap("Authorization", "Token")
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL/*, headerMap*/)
            .withServerHeartbeat(30000)
        resetSubscriptions()
        initChat()
    }

    private fun initChat() {
        resetSubscriptions()

        if (mStompClient != null) {
            val topicSubscribe = mStompClient!!.topic(CHAT_TOPIC)
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ topicMessage: StompMessage ->
                    Log.d(TAG, topicMessage.payload)
                    val message: ChatSocketMessage =
                        gson.fromJson(topicMessage.payload, ChatSocketMessage::class.java)
                    val newMessage = dtoToEntity(message)
                    addMessage(newMessage)
                },
                    {
                        Log.e(TAG, "Error!", it)
                    }
                )

            val lifecycleSubscribe = mStompClient!!.lifecycle()
                .subscribeOn(Schedulers.io(), false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { lifecycleEvent: LifecycleEvent ->
                    when (lifecycleEvent.type!!) {
                        LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened")
                        LifecycleEvent.Type.ERROR -> Log.e(TAG, "Error", lifecycleEvent.exception)
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT,
                        LifecycleEvent.Type.CLOSED -> {
                            Log.d(TAG, "Stomp connection closed")
                        }
                    }
                }

            compositeDisposable!!.add(lifecycleSubscribe)
            compositeDisposable!!.add(topicSubscribe)

            if (!mStompClient!!.isConnected) {
                mStompClient!!.connect()
            }


        } else {
            Log.e(TAG, "mStompClient is null!")
        }
    }

    fun sendMessage(text: String) {
        val message = Message(text = text, author = "Me")
        val chatSocketMessage = entityToDto(message)
        sendCompletable(mStompClient!!.send(CHAT_LINK_SOCKET, gson.toJson(chatSocketMessage)))
        addMessage(message)
    }

    private fun addMessage(message: Message) {
        GlobalScope.launch(Dispatchers.IO) {
            val id: Long = db.messageDao().insert(message)
            message.id = id

        }

        _chatState.value = message
    }

    private fun sendCompletable(request: Completable) {
        compositeDisposable?.add(
            request.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(TAG, "Stomp sended")
                    },
                    {
                        Log.e(TAG, "Stomp error", it)
                    }
                )
        )
    }

    private fun resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }

        compositeDisposable = CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()

        mStompClient?.disconnect()
        compositeDisposable?.dispose()
    }
}