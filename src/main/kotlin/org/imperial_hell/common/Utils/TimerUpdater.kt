package org.imperial_hell.common.Utils

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.imperial_hell.qbrp.Utils.qbTimer

object TimerUpdater {

    var timers = mutableListOf<qbTimer>()

    fun update() {
        timers.forEach { timer -> timer.update() }
    }

    fun registerCycle() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            update()
        }

    }
}