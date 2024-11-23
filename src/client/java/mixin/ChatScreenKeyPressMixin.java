package mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.imperial_hell.ihcore.NetworkCore.Packets.SignalPacket;
import org.imperial_hell.ihcore.NetworkCore.PacketsList;
import org.imperial_hell.ihcore.client.Network.ClientPacketSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChatScreen.class)
public abstract class ChatScreenKeyPressMixin {

    // Флаг для отслеживания, был ли уже отправлен сигнал на первом нажатии клавиши
    private boolean hasSentSignal = false;

    // Перехватываем метод обработки нажатий клавиш
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void onKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Проверяем, что окно чата активно и сигнал еще не был отправлен
        if (client.currentScreen instanceof ChatScreen && !hasSentSignal && keyCode != 256) {
            // Отправляем сигнал, что игрок набирает текст
            ClientPacketSender.INSTANCE.send(PacketsList.INSTANCE.getCHAT_TYPING(), new SignalPacket());

            // Устанавливаем флаг, чтобы сигнал больше не отправлялся, пока не будет отправлено сообщение
            hasSentSignal = true;
        }
    }

    // Перехватываем метод закрытия чата (когда нажимается ESC или закрывается экран)
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void onCloseChat(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Set<Integer> closeKeyCodes = Set.of(257, 335);


        // Проверяем, что нажата клавиша ESC, которая закрывает экран чата
        if (closeKeyCodes.contains(keyCode) && client.currentScreen instanceof ChatScreen) {
            // Сбрасываем флаг, чтобы при следующем открытии чата сигнал отправлялся снова
            ClientPacketSender.INSTANCE.send(PacketsList.INSTANCE.getEND_TYPING(), new SignalPacket());
            hasSentSignal = false;
        }
    }

    // Перехватываем метод отправки сообщения
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void onMessageSent(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> ci) {
        // Когда сообщение отправлено, сбрасываем флаг
        hasSentSignal = false;
    }
}
