package org.imperial_hell.ihcore.client.Core
class Animation(
    private val duration: Long, // Длительность анимации в миллисекундах
    private val animationSpeed: Float = 1.0f, // Скорость изменения прогресса
    private val mode: Mode = Mode.ONCE, // Режим анимации
    private val progressMode: ProgressMode = ProgressMode.LINEAR // Тип интерполяции
) {
    private var startTime: Long = -1L // Время старта анимации
    private var direction: Int = 1 // Направление для режима PING_PONG

    // Вычисляемый прогресс (от 0 до 1)
    val progress: Float
        get() = calculateProgress()

    // Признак, что анимация завершена (для режима ONCE)
    val isFinished: Boolean
        get() = progress >= 1.0f && mode == Mode.ONCE

    // Запуск анимации
    fun start() {
        startTime = System.currentTimeMillis()
    }

    // Перезапуск анимации
    fun restart() {
        startTime = System.currentTimeMillis()
        direction = 1
    }

    // Основной метод расчёта прогресса
    private fun calculateProgress(): Float {
        if (startTime == -1L) return 0f // Если анимация не запущена, прогресс 0

        val curTime = System.currentTimeMillis() - startTime
        var rawProgress = (curTime * animationSpeed) / duration.toFloat()

        // Обработка режима воспроизведения
        when (mode) {
            Mode.ONCE -> {
                rawProgress = rawProgress.coerceIn(0f, 1f) // Ограничиваем в пределах 0-1
            }
            Mode.LOOP -> {
                rawProgress %= 1f // Возвращаемся к началу при превышении
            }
            Mode.PING_PONG -> {
                if (rawProgress >= 1f) {
                    rawProgress = 2f - rawProgress // Переход в обратном направлении
                    direction *= -1
                    restart()
                }
                rawProgress = rawProgress.coerceIn(0f, 1f)
            }
        }

        // Применяем режим прогресса
        return applyProgressMode(rawProgress)
    }

    // Применение интерполяции
    private fun applyProgressMode(rawProgress: Float): Float {
        return when (progressMode) {
            ProgressMode.LINEAR -> rawProgress
            ProgressMode.EASE_IN -> rawProgress * rawProgress
            ProgressMode.EASE_OUT -> rawProgress * (2 - rawProgress)
            ProgressMode.EASE_IN_OUT -> {
                if (rawProgress < 0.5f) {
                    2 * rawProgress * rawProgress
                } else {
                    -1 + (4 - 2 * rawProgress) * rawProgress
                }
            }
        }
    }

    // Режимы анимации
    enum class Mode {
        ONCE, LOOP, PING_PONG
    }

    // Режимы прогресса (интерполяция)
    enum class ProgressMode {
        LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT
    }
}
