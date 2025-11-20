package cc.cassian.raspberry.entity;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.supplementaries.RoseGoldBombExplosion;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.moonlight.api.entity.ImprovedProjectileEntity;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.supplementaries.common.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.common.misc.explosion.BombExplosion;
import net.mehvahdjukaar.supplementaries.common.network.ClientBoundExplosionPacket;
import net.mehvahdjukaar.supplementaries.common.network.ModNetwork;
import net.mehvahdjukaar.supplementaries.common.utils.MiscUtils;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;
import net.mehvahdjukaar.supplementaries.integration.FlanCompat;
import net.mehvahdjukaar.supplementaries.reg.ModParticles;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public class RoseGoldBombEntity extends ImprovedProjectileEntity {

    private final boolean hasFuse = CommonConfigs.Tools.BOMB_FUSE.get() != 0;
    private boolean active = true;
    private int changeTimer = -1;
    private boolean superCharged = false;

    public RoseGoldBombEntity(EntityType<RoseGoldBombEntity> type, Level world) {
        super(type, world);
        this.maxAge = (hasFuse ? CommonConfigs.Tools.BOMB_FUSE.get() : 200);
    }

    public RoseGoldBombEntity(Level worldIn, LivingEntity throwerIn) {
        super(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), throwerIn, worldIn);
        this.maxAge = (hasFuse ? CommonConfigs.Tools.BOMB_FUSE.get() : 200);
    }

    public RoseGoldBombEntity(Level worldIn, double x, double y, double z) {
        super(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), x, y, z, worldIn);
        this.maxAge = (hasFuse ? CommonConfigs.Tools.BOMB_FUSE.get() : 200);
    }

    //data to be saved when the entity gets unloaded
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Active", this.active);
        compound.putInt("Timer", this.changeTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.active = compound.getBoolean("Active");
        this.changeTimer = compound.getInt("Timer");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return PlatHelper.getEntitySpawnPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return RaspberryItems.ROSE_GOLD_BOMB.get();
    }

    @Override
    public ItemStack getItem() {
        return RaspberryItems.ROSE_GOLD_BOMB.get().getDefaultInstance();
    }

    private void spawnBreakParticles() {
        for (int i = 0; i < 8; ++i) {
            this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ModRegistry.BOMB_ITEM.get())), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    public void handleEntityEvent(byte id) {
        switch (id) {
            case 3:
                this.spawnBreakParticles();
                this.discard();
                break;
            case 10:
                this.spawnBreakParticles();
                this.discard();
                break;
            case 67:
                RandomSource random = this.level().getRandom();

                for(int i = 0; i < 10; ++i) {
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + (double)0.25F - (double)(random.nextFloat() * 0.5F), this.getY() + (double)0.45F - (double)(random.nextFloat() * 0.5F), this.getZ() + (double)0.25F - (double)(random.nextFloat() * 0.5F), (double)0.0F, 0.005, (double)0.0F);
                }

                this.active = false;
                break;
            case 68:
                this.level().addParticle(ParticleTypes.SONIC_BOOM, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
                break;
            default:
                super.handleEntityEvent(id);
        }

    }

    @Override
    public boolean hasReachedEndOfLife() {
        return super.hasReachedEndOfLife() || this.changeTimer == 0;
    }

    @Override
    public void tick() {

        //if((this.tickCount-1)%6==0) {
        //    this.playSound(ModSounds.GUNPOWDER_IGNITE.get(), 1.0f,
        //            1.8f + level.getRandom().nextFloat() * 0.2f);
        //}

        if (this.changeTimer > 0) {
            this.changeTimer--;
            level().addParticle(ParticleTypes.SMOKE, this.position().x, this.position().y + 0.5, this.position().z, 0.0D, 0.0D, 0.0D);
        }

        if (this.active && this.isInWater()) {
            this.turnOff();
        }

        super.tick();
    }

    @Override
    public void spawnTrailParticles(Vec3 currentPos, Vec3 newPos) {
        if (this.active && !this.firstTick) {

            double x = currentPos.x;
            double y = currentPos.y;
            double z = currentPos.z;
            double dx = newPos.x - x;
            double dy = newPos.y - y;
            double dz = newPos.z - z;
            int s = 4;
            for (int i = 0; i < s; ++i) {
                double j = i / (double) s;
                this.level().addParticle(ModParticles.BOMB_SMOKE_PARTICLE.get(), x + dx * j, 0.5 + y + dy * j, z + dz * j, 0, 0.02, 0);
            }
        }
    }


    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        hit.getEntity().hurt(hit.getEntity().damageSources().thrown(this, this.getOwner()), 1);
        if (hit.getEntity() instanceof LargeFireball) {
            this.superCharged = true;
            hit.getEntity().remove(RemovalReason.DISCARDED);
        }
    }

    public void turnOff() {
        if (!level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 67);
            level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F, 1.5F);
        }
        this.active = false;
    }

    @Override
    public void playerTouch(Player entityIn) {
        if (!this.level().isClientSide) {
            if (!this.active && entityIn.getInventory().add(this.getItem())) {
                entityIn.take(this, 1);
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        Vec3 vector3d = hit.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vector3d);
        Vec3 vector3d1 = vector3d.normalize().scale(getGravity());
        this.setPosRaw(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
    }

    @Override
    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && !this.hasFuse) {

            //normal explosion
            if (!this.isRemoved()) {
                this.reachedEndOfLife();
            }
        }
    }

    @Override
    protected void updateRotation() {
    }

    public void reachedEndOfLife() {
        Level level = this.level();
        level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.NEUTRAL, 1.5F, 1.5F);
        if (!level.isClientSide) {
            if (this.active) {
                this.createExplosion();
                level.broadcastEntityEvent(this, (byte)10);
            } else {
                level.broadcastEntityEvent(this, (byte)3);
            }

            this.discard();
        }

    }

    private void createExplosion() {
        boolean breaks = false;

        if (this.superCharged) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 6.0F, breaks, Level.ExplosionInteraction.NONE);
        }

        BombExplosion explosion = new RoseGoldBombExplosion(this.level(), this, new RoseGoldBombExplosion.RoseGoldBombExplosionDamageCalculator(), this.getX(), this.getY() + (double)0.25F, this.getZ(), BombEntity.BombType.NORMAL, Explosion.BlockInteraction.KEEP);
        explosion.explode();
        explosion.finalizeExplosion(true);

        for(Player p : this.level().players()) {
            if (p.distanceToSqr(this) < (double)4096.0F && p instanceof ServerPlayer sp) {
                Message message = ClientBoundExplosionPacket.bomb(explosion, p);
                ModNetwork.CHANNEL.sendToClientPlayer(sp, message);
            }
        }

    }
}