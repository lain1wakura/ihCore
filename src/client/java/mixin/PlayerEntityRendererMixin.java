package mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderLabelIfPresent(AbstractClientPlayerEntity player, net.minecraft.text.Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // Получаем UUID игрока
        UUID playerUUID = player.getUuid();

        // Высота рендера иконки (над головой игрока)
        float iconHeight = player.getHeight();

        // Рендер иконки через IconManager
        //IhcoreClient.Companion.getIconManager().renderIcon(matrices, vertexConsumers, playerUUID, light, iconHeight);
    }
}

