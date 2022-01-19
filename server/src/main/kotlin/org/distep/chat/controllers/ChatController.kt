package org.distep.chat.controllers

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.distep.chat.CHAT_TOPIC
import org.distep.chat.LINK_CHAT
import org.distep.chat.dto.ChatSocketMessage
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset


@RestController
@RequestMapping(LINK_CHAT)
class ChatController(
        private val simpleMessageTemplate: SimpMessagingTemplate
) {
    private val logger: Log = LogFactory.getLog(javaClass)

    @MessageMapping("/sock")
    fun chatSocket(res: ChatSocketMessage) {
        sendMessageToUsers(res)
    }

    private fun sendMessageToUsers(message: ChatSocketMessage) {
        if(message.receiver != null) {
            logger.debug("${message.receiver}\t$CHAT_TOPIC\t$message")
            simpleMessageTemplate.convertAndSendToUser(message.receiver!!, CHAT_TOPIC, message)
        } else {
            logger.debug("ALL\t$CHAT_TOPIC\t$message")

            val response = ChatSocketMessage(message.text.reversed(), "Echo bot", LocalDateTime.now(ZoneOffset.UTC))

            simpleMessageTemplate.convertAndSend(CHAT_TOPIC, response)
        }
    }

}