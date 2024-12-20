package org.imperial_hell.qbrp.Sync

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.imperial_hell.common.Proxy.ProxyPlayerData
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.common.Utils.Animation
import java.util.*

class IconManager(private val proxyData: ProximityDataManager) {

    fun renderIcon(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        playerUUID: UUID,
        light: Int,
        height: Float
    ) {
        IhLogger.log("Попытка отрисовать иконку для UUID игрока: <<$playerUUID>>", IhLogger.MessageType.INFO)

        val playerData = proxyData.getPlayerData(playerUUID)
        if (playerData == null) {
            IhLogger.log("Нет данных для UUID игрока: <<$playerUUID>>", IhLogger.MessageType.WARN)
            return
        }

        IhLogger.log("Найденные данные для UUID игрока: <<$playerUUID>>, состояние: ${playerData.state}", IhLogger.MessageType.INFO)
        val iconAnimation: Animation = playerData.iconAnimation

        // Управление анимацией в зависимости от состояния
        when (playerData.state) {
            ProxyPlayerData.State.NONE -> {
                if (iconAnimation.isFinished || !iconAnimation.started) {
                    // Перезапуск анимации назад
                    iconAnimation.restartBackward()
                }

                renderSquare(
                    texture = playerData.icon.texture,
                    matrices = matrices,
                    vertexConsumers = vertexConsumers,
                    light = light,
                    size = 1.0f,
                    xOffset = 0.0f,
                    yOffset = iconAnimation.progress, // Значение прогресса идет вниз
                    overlay = OverlayTexture.DEFAULT_UV
                )
            }

            ProxyPlayerData.State.TYPING_REPLICA -> {
                matrices.push()
                if (!iconAnimation.started) {
                    // Анимация только начинается, направление вверх
                    iconAnimation.restartForward()
                }

                try {
                    matrices.translate(0.0, height.toDouble(), 0.0)
                    alignToCamera(matrices)

                    val scale = 5.5f
                    matrices.scale(scale, scale, scale)

                    renderThreeDots(matrices, vertexConsumers, light, iconAnimation)
                } finally {
                    matrices.pop()
                }
            }

            else -> {
                matrices.push()
                if (!iconAnimation.started) {
                    // Перезапуск анимации для других состояний
                    iconAnimation.restartForward()
                }

                try {
                    matrices.translate(0.0, height.toDouble(), 0.0)
                    alignToCamera(matrices)

                    val scale = 5.5f
                    matrices.scale(scale, scale, scale)

                    renderSquare(
                        texture = playerData.icon.texture,
                        matrices = matrices,
                        vertexConsumers = vertexConsumers,
                        light = light,
                        size = 1.0f,
                        xOffset = 0.0f,
                        yOffset = -1 - iconAnimation.progress, // Анимация теперь учитывает текущий прогресс
                        overlay = OverlayTexture.DEFAULT_UV
                    )
                } finally {
                    matrices.pop()
                }
            }
        }
    }

    private fun alignToCamera(matrices: MatrixStack) {
        val client = MinecraftClient.getInstance()
        val camera = client.gameRenderer.camera
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.yaw))
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.pitch))
    }

    private fun renderSquare(
        texture: Identifier,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
        size: Float,
        xOffset: Float,
        yOffset: Float
    ) {
        val buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture))
        val entry = matrices.peek()
        val positionMatrix = entry.positionMatrix
        val normalMatrix = entry.normalMatrix

        val red = 255
        val green = 255
        val blue = 255
        val alpha = 255

        val halfSize = size / 2

        buffer.vertex(positionMatrix, -halfSize + xOffset, -halfSize + yOffset, 0.0f)
            .color(red, green, blue, alpha)
            .texture(0.0f, 1.0f)
            .overlay(overlay and 0xFFFF, (overlay shr 16) and 0xFFFF)
            .light(light and 0xFFFF, (light shr 16) and 0xFFFF)
            .normal(normalMatrix, 0.0f, 0.0f, -1.0f)
            .next()

        buffer.vertex(positionMatrix, halfSize + xOffset, -halfSize + yOffset, 0.0f)
            .color(red, green, blue, alpha)
            .texture(1.0f, 1.0f)
            .overlay(overlay and 0xFFFF, (overlay shr 16) and 0xFFFF)
            .light(light and 0xFFFF, (light shr 16) and 0xFFFF)
            .normal(normalMatrix, 0.0f, 0.0f, -1.0f)
            .next()

        buffer.vertex(positionMatrix, halfSize + xOffset, halfSize + yOffset, 0.0f)
            .color(red, green, blue, alpha)
            .texture(1.0f, 0.0f)
            .overlay(overlay and 0xFFFF, (overlay shr 16) and 0xFFFF)
            .light(light and 0xFFFF, (light shr 16) and 0xFFFF)
            .normal(normalMatrix, 0.0f, 0.0f, -1.0f)
            .next()

        buffer.vertex(positionMatrix, -halfSize + xOffset, halfSize + yOffset, 0.0f)
            .color(red, green, blue, alpha)
            .texture(0.0f, 0.0f)
            .overlay(overlay and 0xFFFF, (overlay shr 16) and 0xFFFF)
            .light(light and 0xFFFF, (light shr 16) and 0xFFFF)
            .normal(normalMatrix, 0.0f, 0.0f, -1.0f)
            .next()
    }

    private fun renderThreeDots(
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        animation: Animation
    ) {
        val dotSize = 0.04f
        val spacing = 0.045f

        for (i in -1..1) {
            renderSquare(
                texture = Identifier("qbrp", "textures/player_head.png"),
                matrices = matrices,
                vertexConsumers = vertexConsumers,
                light = light,
                size = dotSize,
                xOffset = i * spacing,
                yOffset = -animation.progress,
                overlay = OverlayTexture.DEFAULT_UV
            )
        }
    }
}
