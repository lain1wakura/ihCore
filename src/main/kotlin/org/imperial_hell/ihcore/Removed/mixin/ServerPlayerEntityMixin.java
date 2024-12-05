//package mixin;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.world.World;
//import net.minecraft.util.math.BlockPos;
//import com.mojang.authlib.GameProfile;
//import org.imperial_hell.ihcore.Removed.PlayerNameManager;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(ServerPlayerEntity.class)
//public abstract class ServerPlayerEntityMixin extends PlayerEntity {
//
//    // Конструктор для Mixin
//    protected ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
//        super(world, pos, yaw, gameProfile);
//    }
//
//    // Перехватываем метод getPlayerListName для использования кастомного имени игрока
//    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
//    public void getCustomPlayerListName(CallbackInfoReturnable<Text> callbackInfoReturnable) {
//        // Используем обновленный PlayerNameManager для получения полного имени
//        callbackInfoReturnable.setReturnValue(PlayerNameManager.INSTANCE.getFullPlayerName((ServerPlayerEntity) (Object) this));
//    }
//}
