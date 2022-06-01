package me.shedaniel.linkie.web

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Scopes {
    val IO = CoroutineScope(Dispatchers.IO)
    val Main = Executors.newCachedThreadPool().asCoroutineDispatcher()
}