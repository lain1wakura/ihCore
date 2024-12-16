package org.imperial_hell.qbrp.Utils

import org.imperial_hell.common.Utils.TimerUpdater
import org.imperial_hell.ihSystems.IhLogger

class qbTimer(private val interval: Int, private val callback: () -> Unit) {
    private var tickCounter = 0

    /**
     * Проверяет, достигнут ли интервал, и сбрасывает счетчик, если да.
     *
     * @return true, если интервал достигнут, иначе false
     */
    fun hasReached(): Boolean {
        return tickCounter >= interval
    }

    fun destroy() {
        TimerUpdater.timers.remove(this)
    }

    fun start(): qbTimer {
        if (this in TimerUpdater.timers) {
            IhLogger.log("Попытка запустить таймер не удалась: уже запущен.", IhLogger.MessageType.WARN)
        } else {
            TimerUpdater.timers.add(this)
        }
        return this
    }

    fun update() {
        tickCounter++
        if (tickCounter >= interval) {
            tickCounter = 0
            callback()
        }
    }

    /**
     * Сбрасывает счетчик тиков.
     */
    fun reset() {
        tickCounter = 0
    }

    /**
     * Получает количество оставшихся тиков до следующего срабатывания.
     *
     * @return Количество оставшихся тиков
     */
    fun getRemainingTicks(): Int {
        return interval - tickCounter
    }
}
