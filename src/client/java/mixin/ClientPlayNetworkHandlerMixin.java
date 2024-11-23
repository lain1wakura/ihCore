package mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.imperial_hell.ihcore.client.IhcoreClient;
import org.imperial_hell.ihcore.client.Messages.TypingMessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    // Перехватываем метод обработки входящего сообщения в чат
    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci) {
        if (IhcoreClient.Companion.getTypingMessageManager().isTyping(packet.sender())) {
            IhcoreClient.Companion.getTypingMessageManager().removeTypingPlayer(packet.sender());
        }
    }
}
