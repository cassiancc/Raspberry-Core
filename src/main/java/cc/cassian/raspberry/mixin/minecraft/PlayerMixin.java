package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class PlayerMixin implements PlayerWithGrapplingHook {
    @Unique
    @Nullable
    private GrapplingHookEntity raspberryCore$grapplingHook;

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object)this;

        GrapplingHookEntity hook = this.raspberryCore$grapplingHook;
        if (hook != null && hook.isAttached()) {

            Vec3 hookPos = hook.position();
            Vec3 playerPos = player.position();

            // Don't pull player down
            if (hookPos.y < playerPos.y()) {
                return;
            }

            // Don't pull player if they're standing on the ground
            if (player.isOnGround()) {
                return;
            }

            Vec3 rope = hookPos.subtract(playerPos);
            double distanceSqr = rope.lengthSqr();

            double targetLength = 1.5F;
            double targetLengthSqr = targetLength * targetLength;

            double stiffness = 0.02D;
            double maxPull = 0.15;

            if (distanceSqr > targetLengthSqr) {
                double pull = Math.min(distanceSqr/targetLengthSqr * stiffness, maxPull);
                Vec3 movement = rope.normalize().multiply(pull,pull * 1.1, pull);
                player.setDeltaMovement(player.getDeltaMovement().add(movement));
            }

            player.hasImpulse = true;

            if (!player.level.isClientSide) {
                player.resetFallDistance();
                if (!player.isOnGround()) player.hurtMarked = false;
            }
        }
    }

    @Override
    public @Nullable GrapplingHookEntity raspberryCore$getHook() {
        return this.raspberryCore$grapplingHook;
    }

    @Override
    public void raspberryCore$setHook(@Nullable GrapplingHookEntity hookEntity) {
        this.raspberryCore$grapplingHook = hookEntity;
    }
}

