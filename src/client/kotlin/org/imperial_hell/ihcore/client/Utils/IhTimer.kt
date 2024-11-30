package org.imperial_hell.ihcore.Utils

class IhTimer(private val interval: Int) {
    private var tickCounter = 0

    /**
     * Проверяет, достигнут ли интервал, и сбрасывает счетчик, если да.
     *
     * @return true, если интервал достигнут, иначе false
     */
    fun hasReached(): Boolean {
        tickCounter++
        if (tickCounter >= interval) {
            tickCounter = 0
            return true
        }
        return false
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
