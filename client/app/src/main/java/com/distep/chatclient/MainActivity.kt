package com.distep.chatclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.distep.chatclient.data.db.AppDb
import com.distep.chatclient.data.entity.Message
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var db: AppDb

    @Inject
    lateinit var mainViewModel: MainViewModel

    private var viewPager2: ViewPager2? = null
    private var recyclerView: RecyclerView? = null
    private var recordAdapter: RecordAdapter? = null

    private var pagedListLiveData: LiveData<PagedList<Message>>? = null

    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(10)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textField = text_field
        send_message_btn.setOnClickListener {
            mainViewModel.sendMessage(textField.text.toString())
        }

        initRecyclerView()
    }

    override fun onStart() {
        super.onStart()

        mainViewModel.liveChatState.observe(this){
            if(it != null) {
                recordAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initRecyclerView() {
        viewPager2 = chat_content
        viewPager2!!.orientation = ViewPager2.ORIENTATION_VERTICAL

        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        recyclerView = field.get(viewPager2) as RecyclerView
        recyclerView!!.clearOnChildAttachStateChangeListeners()
        recyclerView!!.layoutManager


        setAdapter(true, config)
    }

    private fun setAdapter(init: Boolean, config: PagedList.Config) {
        val factory: DataSource.Factory<Int, Message> = db.messageDao().getItems()

        pagedListLiveData?.removeObservers(this)
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .build()

        pagedListLiveData?.observe(this) { results -> recordAdapter?.submitList(results) }

        recordAdapter = RecordAdapter()
        viewPager2!!.adapter = recordAdapter!!
        if (!init) {
            recyclerView!!.swapAdapter(recordAdapter!!, true)
        }
    }
}