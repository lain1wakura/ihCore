package org.imperial_hell.common.Utils

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.imperial_hell.qbrp.Utils.qbTimer
import java.util.concurrent.CopyOnWriteArrayList

object TimerUpdater {

    var timers = CopyOnWriteArrayList<qbTimer>()

    fun update() {
        timers.forEach { timer -> timer.update() }
    }

    fun registerCycle() {
        ServerTickEvents.END_WORLD_TICK.register { server ->
            update()
        }

    }
}