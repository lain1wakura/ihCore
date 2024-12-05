/*package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.imperial_hell.ihcore.Sync.ProximityPlayerData;
import org.imperial_hell.ihcore.client.IhcoreClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Unique
    private float easeOutQuad(float t) {
        return 1 - (1 - t) * (1 - t); // Для плавного появления
    }

    @Unique
    private float easeInQuad(float t) {
        return t * t; // Для плавного исчезновения
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderTypingIndicator(
            AbstractClientPlayerEntity player,
            Text text,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo callbackInfo
    ) {
        // Получаем текущее состояние — печатает игрок или нет
        boolean isTyping = IhcoreClient.Companion.getProximityDataManager().getPlayerData(player.getUuid()).getState() == ProximityPlayerData.State.TYPING_COMMAND;

        // Время начала и конца набора
        long startTime = IhcoreClient.Companion.getTypingMessageManager().getStartTime(player.getUuid());
        long currentTime = System.currentTimeMillis();
        long dotCycle = IhcoreClient.Companion.getTypingMessageManager().getDotCycle();

        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

        // Если игрок печатает, анимация появления
        float elapsedTime;
        if (isTyping) {
            elapsedTime = currentTime - startTime;
        } else {
            // Если игрок перестал печатать, используем обратную анимацию
            long endTime = IhcoreClient.Companion.getTypingMessageManager().getEndTime(player.getUuid());
            elapsedTime = Math.max(450.0f - (currentTime - endTime), 0f); // обратная анимация
        }

        // Анимация длится 450 мс, независимо от направления
        float animationProgress = Math.min(elapsedTime / 450.0f, 1.0f);
        float elapsedDotTime = currentTime - dotCycle;
        float dotProgress = Math.min(elapsedDotTime / 600.0f, 1.0f);
        if (dotProgress == 1) {
            IhcoreClient.Companion.getTypingMessageManager().updateDotCycle();
        }

        int maxDots = 4;  // Максимальное количество символов
        int numDots = (int) (dotProgress / ((float) 1 / maxDots));  // Количество точек на основе прогресса

        StringBuilder typingTextBuilder = new StringBuilder();
        for (int i = 0; i < maxDots; i++) {
            if (i < numDots) {
                typingTextBuilder.append(".");  // Добавляем точку
            } else {
                typingTextBuilder.append(" ");  // Добавляем пробел вместо точки
            }
        }

        String typingText = typingTextBuilder.toString();

        float smoothAnimationProgress;

        if (isTyping) {
            // Применяем функцию Ease Out (для появления)
            smoothAnimationProgress = easeOutQuad(animationProgress);
        } else {
            // Применяем функцию Ease In (для исчезновения)
            smoothAnimationProgress = easeInQuad(animationProgress);
        }

        // Прозрачность на основе smoothAnimationProgress
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(smoothAnimationProgress * 0.25F);
        int backgroundOpacity = (int) (g * 255.0F) << 24;
        int textOpacity = (int) (smoothAnimationProgress * 255.0F) << 24;

        // Смещение по высоте
        float startOffset = -0.4F; // Начальная позиция ниже головы игрока
        float endOffset = 0.43F; // Конечная позиция над головой игрока
        float additionalHeight = startOffset + (endOffset * smoothAnimationProgress);

        boolean bl = !player.isSneaky();
        float f = player.getNameLabelHeight() + additionalHeight; // Добавляем смещение к высоте
        int i = 0;

        // Прекращаем рендеринг, если анимация завершена и текст полностью невидим
        if (smoothAnimationProgress <= 0) {
            callbackInfo.cancel(); // Останавливаем рендеринг текста, если он полностью исчез
            return; // Убедитесь, что дальнейшее выполнение не происходит
        }

        matrices.push();
        matrices.translate(0.0F, f, 0.0F); // Применяем смещение по высоте
        matrices.multiply(dispatcher.getRotation());
        matrices.scale(-0.030F, -0.030F, 0.030F);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float h = (float) (-textRenderer.getWidth(typingText) / 2);
        float w = (float) (-textRenderer.getWidth(typingText) / 3);

        // Применяем прозрачность к тексту
        int textColor = (0xFFC5A8) | textOpacity; // Белый цвет с изменяемой прозрачностью

        textRenderer.draw(typingText, h, (float) i, textColor, false, matrix4f, vertexConsumers,
                bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, backgroundOpacity, light);


        matrices.pop();
        callbackInfo.cancel();
    }

}*/
