package org.imperial_hell.qbrp.client.Game

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.LightType
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Blocks.BlockData.Overlays.Overlay
import java.util.concurrent.ConcurrentHashMap

object OverlayManager {

    private val overlays = ConcurrentHashMap<BlockPos, Overlay>() // карта: блоковая позиция -> оверлей
    private val quadUv = listOf<Pair<Float, Float>>(Pair(0.0f, 0.0f), Pair(1.0f, 0.0f), Pair(1.0f, 1.0f), Pair(0.0f, 1.0f))

    fun addOverlay(blockPos: BlockPos, overlay: Overlay) {
        overlays[blockPos] = overlay
    }

    fun clear() {
        overlays.clear()
    }

    fun renderOverlays(context: WorldRenderContext) {
        val matrices = context.matrixStack()
        val camera = context.camera()
        val world = context.world()

        val overlaySnapshot = overlays.toMap()
        for ((blockPos, overlay) in overlaySnapshot) {
            //println("Rendering overlay at: $blockPos with side: ${overlay.side}")
            val blockState = world.getBlockState(blockPos)

            if (blockState.isOpaque) { // Проверяем блок, чтобы не накладывать текстуры на воздух
                val vertexConsumer = context.consumers()?.getBuffer(RenderLayer.getEntityTranslucent(overlay.texture))
                if (vertexConsumer == null) {
                    //println("VertexConsumer is null for overlay: ${overlay.texture}")
                    continue
                }
                val cameraPos: Vec3d = camera.pos

                // Calculate side-specific translation offsets
                val (xOffset, yOffset, zOffset) = when (overlay.side) {
                    "top" -> Triple(0.0, 0.02, 0.0)
                    "bottom" -> Triple(0.0, -0.02, 0.0)
                    "north" -> Triple(0.0, 0.0, 0.02)
                    "south" -> Triple(0.0, 0.0, -0.02)
                    "east" -> Triple(0.02, 0.0, 0.0)
                    "west" -> Triple(-0.02, 0.0, 0.0)
                    else -> Triple(0.0, 0.0, 0.0) // default offset
                }

                val light = world.getLightLevel(LightType.BLOCK, BlockPos(((blockPos.x + (xOffset * 50)).toInt()), ((blockPos.y + (yOffset * 50)).toInt()), ((blockPos.z + (zOffset * 50)).toInt())))
                var sky = world.getLightLevel(LightType.SKY, BlockPos(((blockPos.x + (xOffset * 50)).toInt()), ((blockPos.y + (yOffset * 50)).toInt()), ((blockPos.z + (zOffset * 50)).toInt())))

                // Determine normals based on side
                val (normalX, normalY, normalZ) = when (overlay.side) {
                    "top" -> Triple(0.0, 1.0, 0.0)    // Нормаль вверх
                    "bottom" -> Triple(0.0, -1.0, 0.0) // Нормаль вниз
                    "north" -> Triple(0.0, 0.0, -1.0) // Нормаль на север
                    "south" -> Triple(0.0, 0.0, 1.0)  // Нормаль на юг
                    "east" -> Triple(1.0, 0.0, 0.0)   // Нормаль на восток
                    "west" -> Triple(-1.0, 0.0, 0.0)  // Нормаль на запад
                    else -> Triple(0.0, 0.0, 0.0)     // Нулевая нормаль (по умолчанию)
                }

                val vertices = when (overlay.side) {
                    "top" -> listOf(
                        Triple(0f, 1f, 0f), // Нижний левый угол
                        Triple(1f, 1f, 0f), // Нижний правый угол
                        Triple(1f, 1f, 1f), // Верхний правый угол
                        Triple(0f, 1f, 1f)  // Верхний левый угол
                    )
                    "bottom" -> listOf(
                        Triple(0f, 0f, 1f),
                        Triple(1f, 0f, 1f),
                        Triple(1f, 0f, 0f),
                        Triple(0f, 0f, 0f)
                    )
                    "north" -> listOf(
                        Triple(0f, 0f, 1f),
                        Triple(0f, 1f, 1f),
                        Triple(1f, 1f, 1f),
                        Triple(1f, 0f, 1f),
                    )
                    "south" -> listOf(
                        Triple(1f, 0f, 0f),
                        Triple(0f, 0f, 0f),
                        Triple(0f, 1f, 0f),
                        Triple(1f, 1f, 0f)
                    )
                    "east" -> listOf(
                        Triple(1f, 0f, 0f),
                        Triple(1f, 0f, 1f),
                        Triple(1f, 1f, 1f),
                        Triple(1f, 1f, 0f)
                    )
                    "west" -> listOf(
                        Triple(0f, 0f, 1f),
                        Triple(0f, 0f, 0f),
                        Triple(0f, 1f, 0f),
                        Triple(0f, 1f, 1f)
                    )
                    else -> emptyList() // Пустой список, если сторона не указана
                }

                matrices.push()

                // Translate the matrix based on the calculated offsets
                matrices.translate(
                    blockPos.x - cameraPos.x + xOffset,
                    blockPos.y - cameraPos.y + yOffset,
                    blockPos.z - cameraPos.z + zOffset
                )

                //matrices.scale(0.5f, 1f, 0.5f)

                // Рисуем текстуру поверх блока для определенной стороны
                drawQuad(matrices, vertexConsumer, OverlayTexture.DEFAULT_UV, light, sky, normalX, normalY, normalZ, vertices)

                matrices.pop()
            }
        }
    }

    private fun drawQuad(
        matrices: MatrixStack,
        vertexConsumer: VertexConsumer,
        overlay: Int,
        light: Int,
        sky: Int,
        normalX: Double,
        normalY: Double,
        normalZ: Double,
        vertices: List<Triple<Float, Float, Float>>
    ) {
        renderQuad(matrices, vertexConsumer, overlay, light, sky, normalX.toFloat(), normalY.toFloat(), normalZ.toFloat(), vertices)
    }

    private fun renderQuad(
        matrices: MatrixStack,
        vertexConsumer: VertexConsumer,
        overlay: Int,
        light: Int,
        sky: Int,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        vertices: List<Triple<Float, Float, Float>>
    ) {
        val matrix = matrices.peek().positionMatrix
        var i = 0
        for ((x, y, z) in vertices) {

            vertexConsumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .texture(
                    quadUv[i].first,
                    quadUv[i].second
                ) // Используем координаты для текстуры (можно настроить иначе)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.pack(light, sky))
                .normal(matrices.peek().normalMatrix, normalX, normalY, normalZ)
                .next()
            i++
        }
    }

}