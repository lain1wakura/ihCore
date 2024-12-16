package org.imperial_hell.qbrp.System

class Animation(
    private val duration: Double,
    private val animationSpeed: Float = 1.0f,
    val mode: Mode = Mode.ONCE,
    private val progressMode: ProgressMode = ProgressMode.LINEAR
) {
    private var startTime: Long = -1L
    private var _direction: Int = 1
    private var onFinish: (() -> Unit)? = null
    var started: Boolean = false

    // Direction property
    var direction: Int
        get() = _direction
        set(value) {
            if (value == 1 || value == -1) {
                _direction = value
            } else {
                throw IllegalArgumentException("Direction must be 1 or -1")
            }
        }

    // Current progress (0 to 1)
    val progress: Float
        get() = calculateProgress()

    // Is animation finished
    val isFinished: Boolean
        get() = progress >= 1.0f && mode == Mode.ONCE

    // Start animation
    fun start() {
        started = true
        startTime = System.currentTimeMillis()
    }

    // Restart animation
    fun restart() {
        started = true
        startTime = System.currentTimeMillis()
    }

    // Restart in forward direction
    fun restartForward() {
        direction = 1
        restart()
    }

    // Restart in backward direction
    fun restartBackward() {
        direction = -1
        restart()
    }

    // Reset animation
    fun reset() {
        started = false
        startTime = -1L
    }

    // Reverse direction
    fun reverse() {
        direction *= -1
    }

    // Set forward direction
    fun setForward() {
        direction = 1
    }

    // Set backward direction
    fun setBackward() {
        direction = -1
    }

    // Listener for animation finish
    fun setOnFinishListener(listener: () -> Unit) {
        onFinish = listener
    }

    private fun calculateProgress(): Float {
        if (startTime == -1L) return 0f

        val curTime = System.currentTimeMillis() - startTime
        var rawProgress = (curTime * animationSpeed) / duration.toFloat()

        rawProgress *= direction

        when (mode) {
            Mode.ONCE -> {
                rawProgress = rawProgress.coerceIn(0f, 1f)
                if (rawProgress >= 1f) {
                    started = false
                    onFinish?.invoke()
                }
            }
            Mode.LOOP -> {
                rawProgress %= 1f
                if (rawProgress < 0f) rawProgress += 1f
            }
            Mode.PING_PONG -> {
                if (rawProgress >= 1f || rawProgress <= 0f) {
                    direction *= -1
                    rawProgress = rawProgress.coerceIn(0f, 1f)
                }
            }
        }

        return applyProgressMode(rawProgress)
    }

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

    // Animation modes
    enum class Mode {
        ONCE, LOOP, PING_PONG
    }

    // Progress modes (interpolation)
    enum class ProgressMode {
        LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT
    }
}
