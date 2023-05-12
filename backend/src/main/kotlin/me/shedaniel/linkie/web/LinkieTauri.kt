package me.shedaniel.linkie.web

import me.shedaniel.linkie.web.deps.depsCycle
import me.shedaniel.linkie.web.deps.startDepsCycle
import me.shedaniel.linkie.web.deps.startLinkie

fun main() {
    depsCycle()
    startDepsCycle()
    startLinkie()
}