package org.distep.chat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatOnWebsocketApplication

fun main(args: Array<String>) {
	runApplication<ChatOnWebsocketApplication>(*args)
}
