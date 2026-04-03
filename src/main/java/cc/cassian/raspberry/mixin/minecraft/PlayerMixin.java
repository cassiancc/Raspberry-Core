package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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

            if (!hasHookedEntity && !hook.isSticky) {
                // Don't pull player down
                if (hookPos.y < playerPos.y()) {
                    return;
                }

                // Don't pull player if they're standing on the ground
                if (player.isOnGround()) {
                    return;
                }
            }
            boolean isJumping = ((LivingEntityAccessor) this).isJumping();

            if (!hook.isSticky) {
                if (player.isCrouching()) {
                    hook.setShouldPull(false);
                } else if (isJumping) {
                    hook.setShouldPull(true);
                }
            }

            // Climb up side of blocks
            boolean hasClimbingShoes = player.getItemBySlot(EquipmentSlot.FEET).is(RaspberryTags.GRAPPLING_HOOK_WALL_CLIMBING);
            if (hasClimbingShoes) {
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
            }

            // Push off side of blocks
            if (isJumping && !player.isOnGround() && player.horizontalCollision && !hasHookedEntity) {
                Vec3 forward = player.getForward();
                ClipContext context = new ClipContext(
                        playerPos,
                        playerPos.add(forward),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                );
                BlockHitResult blockHitResult = player.level.clip(context);
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    Vec3i hitNormal = blockHitResult.getDirection().getNormal();
                    Vec3 pushVector = forward.reverse().add(hitNormal.getX(), hitNormal.getY(), hitNormal.getZ()).normalize().scale(0.5);
                    player.setDeltaMovement(player.getDeltaMovement().add(pushVector));
                }
            }

            Vec3 velocity = player.getDeltaMovement();
            double targetLength = 1.5F;
            double maxPull = 0.15;
            double stiffness = 0.01D;

            if (hook.isSticky) {
                stiffness = 0.5F;
                maxPull = 0.2F;
                targetLength = 2.0F;
            }

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

                // Extend rope by adding back some radial movement
                double maxLengthSqr = 256; // 16sqr
                if (!hook.isSticky && player.isCrouching() && !player.isOnGround() && distanceSqr < maxLengthSqr){
                    Vec3 addedRadialMovement = radialMovement.scale(0.7);
                    player.setDeltaMovement(player.getDeltaMovement().add(addedRadialMovement));
                }

                if (!hook.getShouldPull() && !player.horizontalCollision) {
                    // Negate some dampening for better swinging
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.085, 1.01, 1.085));
                }

                // Pull towards hook
                if(hook.getShouldPull() || hasHookedEntity){
                    double pullDistance = Math.max(distanceSqr - targetLengthSqr, 0);
                    double pull = Math.min(pullDistance * stiffness, maxPull);
                    Vec3 pullVector = ropeDirection.reverse().scale(pull);

                    player.setDeltaMovement(player.getDeltaMovement().add(pullVector));
                }

                player.hasImpulse = true;
            }

            if (!player.level.isClientSide) {
                if (hookPos.y > playerPos.y() + 2) {
                    player.resetFallDistance();
                    if (!player.isOnGround()) player.hurtMarked = false;
                }
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

