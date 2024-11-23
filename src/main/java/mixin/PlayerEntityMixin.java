package mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.imperial_hell.ihcore.Characters.System.PlayerNameManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(
            method = "getDisplayName",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;")
    )
    private Text setCustomName(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            // Используем обновленный PlayerNameManager для получения полного имени игрока
            return PlayerNameManager.INSTANCE.getFullPlayerName((ServerPlayerEntity) player);
        } else {
            return player.getName();
        }
    }
}
