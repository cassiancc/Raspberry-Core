package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
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
        if (hook != null && (hook.isAttached() || hook.getHookedIn() != null)) {
            Vec3 hookPos = hook.position();
            Vec3 playerPos = player.position();
            boolean hasHookedEntity = hook.getHookedIn() != null;
            if (hasHookedEntity) {
                playerPos = player.getEyePosition();
            }
            Vec3 rope = hookPos.subtract(playerPos);
            Vec3 ropeDirection = rope.normalize().reverse();
            double distanceSqr = rope.lengthSqr();

            if (!hasHookedEntity) {
                // Don't pull player down
                if (hookPos.y < playerPos.y()) {
                    return;
                }

                // Don't pull player if they're standing on the ground
                if (player.isOnGround()) {
                    return;
                }
            }

            // Climb up side of blocks
            double travelVectorLengthSqr = travelVector.lengthSqr();
            if (!player.isCrouching() && travelVectorLengthSqr > 0.01) {
                Vec3 vec3 = travelVector.normalize();
                float yRotSin = Mth.sin(player.getYRot() * ((float)Math.PI / 180F));
                float yRotCos = Mth.cos(player.getYRot() * ((float)Math.PI / 180F));
                Vec3 travelDirectionVector = new Vec3(vec3.x * (double)yRotCos - vec3.z * (double)yRotSin, 0, vec3.z * (double)yRotCos + vec3.x * (double)yRotSin);

                double dot = travelDirectionVector.dot(new Vec3(ropeDirection.reverse().x, 0, ropeDirection.reverse().z));

                if (dot > 0) {
                    if (player.horizontalCollision) {
                        player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, Math.max(player.getDeltaMovement().y, 0.2), player.getDeltaMovement().z));
                    }
                }
            }

            Vec3 velocity = player.getDeltaMovement();
            double targetLength = 1.5F;
            double maxPull = 0.15;
            double stiffness = 0.01D;

            if (hasHookedEntity) {
                Entity entity = hook.getHookedIn();
                targetLength = targetLength * GrapplingHookEntity.getSizeRatio(entity, player);
                maxPull = maxPull * GrapplingHookEntity.getPullingRatio(entity, player, true);
            }

            double targetLengthSqr = targetLength * targetLength;

            if (distanceSqr > targetLengthSqr) {
                // Negate any radial velocity away from the hook
                double radialVelocity = Math.max(velocity.dot(ropeDirection), 0);
                Vec3 radialMovement = ropeDirection.scale(radialVelocity);
                Vec3 tangentialMovement = velocity.subtract(radialMovement);

                player.setDeltaMovement(tangentialMovement);

                if (player.isCrouching() && !player.isOnGround()) {
                    // Add back some radial movement, but only if there's little or no tangential movement
                    Vec3 addedRadialMovement = radialMovement.scale(Math.max(0.7 - (tangentialMovement.length() * 3), 0));
                    player.setDeltaMovement(player.getDeltaMovement().add(addedRadialMovement));
                    if (!player.horizontalCollision) {
                        // Negate some dampening for better swinging
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1.085, 1.01, 1.085));
                    }
                };

                // Pull towards hook
                if(!player.isCrouching() || hasHookedEntity){
                    double pullDistance = Math.max(distanceSqr - targetLengthSqr, 0);
                    double pull = Math.min(pullDistance * stiffness, maxPull);
                    Vec3 pullVector = ropeDirection.reverse().scale(pull);

                    player.setDeltaMovement(player.getDeltaMovement().add(pullVector));
                }

                player.hasImpulse = true;
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

