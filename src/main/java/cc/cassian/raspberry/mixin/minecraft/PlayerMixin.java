package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.core.Vec3i;
import net.minecraft.stats.Stats;
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
    private int raspberryCore$noJumpDelay;

    @Unique
    @Nullable
    private GrapplingHookEntity raspberryCore$grapplingHook;

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void jumpFromGround(CallbackInfo ci) {
        this.raspberryCore$noJumpDelay = 10;
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object)this;
        GrapplingHookEntity hook = this.raspberryCore$grapplingHook;

        if (this.raspberryCore$noJumpDelay > 0) {
            --this.raspberryCore$noJumpDelay;
        }

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

            // Don't pull player down
            if (hookPos.y < playerPos.y() + 0.5) {
                return;
            }

            if (!hasHookedEntity && !hook.isSticky) {
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
            if (isJumping && raspberryCore$noJumpDelay == 0 && player.horizontalCollision && !player.verticalCollisionBelow && !player.isOnGround() && !hasHookedEntity) {
                double jumpPower = ((LivingEntityAccessor) this).callGetJumpPower();
                Vec3 forward = player.getForward().multiply(1, 0, 1);
                ClipContext context = new ClipContext(
                        player.position().add(0,0.5,0),
                        player.position().add(0,0.5,0).add(forward),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                );
                BlockHitResult blockHitResult = player.level.clip(context);
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    Vec3i hitNormal = blockHitResult.getDirection().getNormal();
                    Vec3 pushVector = forward.reverse().add(hitNormal.getX(), hitNormal.getY(), hitNormal.getZ()).normalize().scale(jumpPower);
                    player.setDeltaMovement(player.getDeltaMovement().add(pushVector));
                    this.raspberryCore$noJumpDelay = 10;
                    player.awardStat(Stats.JUMP);
                    player.causeFoodExhaustion(0.05F);
                }
            }

            Vec3 velocity = player.getDeltaMovement();
            double targetLength = hook.TARGET_LENGTH;
            double maxPull = 0.15;
            double stiffness = 0.02D;

            if (hook.isSticky) {
                stiffness = 0.5F;
                maxPull = 0.2F;
                targetLength = hook.TARGET_LENGTH_STICKY;
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
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.08, 1.01, 1.08));
                }

                // Pull towards hook
                if(hook.getShouldPull() || hasHookedEntity){
                    double pullDistance = Math.max(distanceSqr - targetLengthSqr, 0);
                    double pull = Math.min(pullDistance * stiffness, maxPull);
                    Vec3 pullVector = ropeDirection.reverse().scale(pull);
                    hook.addPull(pull);
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

