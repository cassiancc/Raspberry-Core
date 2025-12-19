package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonFireball.class)
public abstract class DragonFireballMixin extends AbstractHurtingProjectile {

    protected DragonFireballMixin(net.minecraft.world.entity.EntityType<? extends AbstractHurtingProjectile> entityType, net.minecraft.world.level.Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isPickable() {
        return ModConfig.get().ghastDragonFireball || super.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!ModConfig.get().ghastDragonFireball) {
            return super.hurt(source, amount);
        }

        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markHurt();
            Entity attacker = source.getEntity();
            
            if (attacker != null) {
                Vec3 look = attacker.getLookAngle();
                this.setDeltaMovement(look);
                this.xPower = look.x * 0.1D;
                this.yPower = look.y * 0.1D;
                this.zPower = look.z * 0.1D;
                this.setOwner(attacker);
                return true;
            } else {
                return false;
            }
        }
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void raspberry$onHit(HitResult result, CallbackInfo ci) {
        if (!ModConfig.get().ghastDragonFireball) {
            return; 
        }
        ci.cancel();

        Entity owner = this.getOwner();
            
        if (result instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            
            DamageSource src = new IndirectEntityDamageSource("fireball", this, owner).setProjectile();

            if (target instanceof Ghast) {
                target.hurt(src, 30.0F); 
            } else {
                target.hurt(src, 6.0F);  
            }
            
            this.discard();
            return;
        }

        if (!this.level.isClientSide) {
            var cloud = new net.minecraft.world.entity.AreaEffectCloud(
                this.level, this.getX(), this.getY(), this.getZ());

            if (owner instanceof net.minecraft.world.entity.LivingEntity living) {
                cloud.setOwner(living);
            }

            cloud.setParticle(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH);
            cloud.setRadius(3.0F);
            cloud.setDuration(100); 
            cloud.setRadiusPerTick((7.0F - cloud.getRadius()) / cloud.getDuration());

            cloud.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.HARM, 1, 1));

            this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            
            this.level.addFreshEntity(cloud);
        }

        this.discard();
    }

}