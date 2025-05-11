package cc.cassian.raspberry.entity;

import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.supplementaries.reg.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Ashball extends ThrowableItemProjectile {
    public Ashball(EntityType<Ashball> entityType, Level level) {
        super(entityType, level);
    }

    public Ashball(Level level, LivingEntity shooter) {
        super(RaspberryEntityTypes.ASHBALL.get(), shooter, level);
    }

    public Ashball(Level level, double x, double y, double z) {
        super(RaspberryEntityTypes.ASHBALL.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return RaspberryItems.ASHBALL.get();
    }

    private ParticleOptions getParticle() {
        return ModParticles.ASH_PARTICLE.get();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleOptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level.addParticle(particleOptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isAffectedByPotions()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }
}
