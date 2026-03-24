package cc.cassian.raspberry.entity;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrapplingHookEntity extends Projectile implements IEntityAdditionalSpawnData {
    private final int RANGE = 64;
    private final int RANGE_SQR = RANGE * RANGE;
    protected boolean isAttached;
    private final ItemStack fishingLine;
    private final ItemStack bobber;
    private final ItemStack fishingRod;
    private final int luck;
    public int shakeTime;

    @SuppressWarnings("unchecked")
    public GrapplingHookEntity(PlayMessages.SpawnEntity spawnPacket, Level level) {
        super((EntityType) RaspberryEntityTypes.GRAPPLING_HOOK.get(), level);
        this.noCulling = true;

        this.setOwner(level.getPlayerByUUID(spawnPacket.getAdditionalData().readUUID()));
        FriendlyByteBuf buf = spawnPacket.getAdditionalData();
        this.luck = buf.readInt();
        this.fishingLine = buf.readItem();
        this.bobber = buf.readItem();
        this.fishingRod = buf.readItem();
    }

    private GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level, int luck, int lureSpeed, @Nonnull ItemStack fishingLine, @Nonnull ItemStack bobber, @Nonnull ItemStack rod) {
        super(entityType, level);
        this.noCulling = true;

        this.luck = Math.max(0, luck);
        this.fishingLine = fishingLine;
        this.bobber = bobber;
        this.fishingRod = rod;
    }

    @SuppressWarnings("unchecked")
    public GrapplingHookEntity(Player player, Level level, int luck, int lureSpeed, @Nonnull ItemStack fishingLine, @Nonnull ItemStack bobber, @Nonnull ItemStack rod) {
        this((EntityType) RaspberryEntityTypes.GRAPPLING_HOOK.get(), level, luck, lureSpeed, fishingLine, bobber, rod);
        this.setOwner(player);
        this.moveTo(player.getX(), player.getEyeY(),  player.getZ(), 0.0F, 0.0F);

        float playerXRot = player.getXRot();
        float playerYRot = player.getYRot();
        float x = -Mth.sin(playerYRot * ((float)Math.PI / 180F)) * Mth.cos(playerXRot * ((float)Math.PI / 180F));
        float y = -Mth.sin((playerXRot) * ((float)Math.PI / 180F));
        float z = Mth.cos(playerYRot * ((float)Math.PI / 180F)) * Mth.cos(playerXRot * ((float)Math.PI / 180F));
        Vec3 direction = new Vec3(x, y, z).normalize();

        double velocity = 1.3;
        Vec3 playerMovement = player.getDeltaMovement();
        this.setDeltaMovement(direction.scale(velocity).add(playerMovement.x, player.isOnGround() ? 0 : playerMovement.y, playerMovement.z));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < RANGE_SQR;
    }

    public boolean isAttached() {
        return isAttached;
    }

    public void tick() {
        super.tick();
        Player player = this.getPlayerOwner();
        if (player == null) {
            this.discard();
        } else if (this.level.isClientSide || !this.shouldStopFishing(player)) {

            Vec3 deltaMovement = this.getDeltaMovement();

            if (this.shakeTime > 0) {
                --this.shakeTime;
            }

            // Set initial rotation
            if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
                this.setYRot((float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * (double)(180F / (float)Math.PI)));
                this.setXRot((float)(Mth.atan2(deltaMovement.y, deltaMovement.horizontalDistance()) * (double)(180F / (float)Math.PI)));
                this.yRotO = this.getYRot();
                this.xRotO = this.getXRot();
            }

            if (!isAttached) {
                Vec3 currentPosition = this.position();
                Vec3 movedPosition = currentPosition.add(deltaMovement);

                // Check for rope collision with blocks
                ClipContext context = new ClipContext(
                        currentPosition,
                        player.getEyePosition(),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        this
                );
                BlockHitResult blockEdgeHitResult = level.clip(context);

                if (blockEdgeHitResult.getType() == HitResult.Type.BLOCK) {
                    if (blockEdgeHitResult.getDirection() == Direction.DOWN) {
                        this.breakRope(player);
                    } else if (blockEdgeHitResult.getDirection() != Direction.UP) {
                        // Line of sight from hook to player collides with side of block
                        // Pretend we hit the top to avoid bounce off
                        blockEdgeHitResult = new BlockHitResult(blockEdgeHitResult.getLocation(), Direction.UP, blockEdgeHitResult.getBlockPos(), blockEdgeHitResult.isInside());
                    }

                    this.onHit(blockEdgeHitResult);
                    this.hasImpulse = true;
                } else {
                    // Check for hook collision with block
                    HitResult blockHitResult = this.level.clip(new ClipContext(currentPosition, movedPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                        this.onHit(blockHitResult);
                        this.hasImpulse = true;
                    }
                }

                // Stop updating movement if attached
                if (this.isAttached) {
                    return;
                }

                deltaMovement = this.getDeltaMovement();
                double deltaX = deltaMovement.x;
                double deltaY = deltaMovement.y;
                double deltaZ = deltaMovement.z;

                // If closer than one block
                if (this.distanceToSqr(player.getEyePosition()) <= 1.0) {
                    // Rotate to align with movement direction
                    this.setYRot((float)(Mth.atan2(deltaX, deltaZ) * (double)(180F / (float)Math.PI)));
                    this.setXRot((float)(Mth.atan2(deltaY, deltaMovement.horizontalDistance()) * (double)(180F / (float)Math.PI)));
                } else {
                    // Rotate to align with line of sight
                    Vec3 ropeVec = player.getEyePosition().subtract(this.position()).reverse();
                    this.setYRot((float)(Mth.atan2(ropeVec.x, ropeVec.z) * (double)(180F / (float)Math.PI)));
                    this.setXRot((float)(Mth.atan2(ropeVec.y, ropeVec.horizontalDistance()) * (double)(180F / (float)Math.PI)));
                };

                this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
                this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

                double newX = this.getX() + deltaX;
                double newY = this.getY() + deltaY;
                double newZ = this.getZ() + deltaZ;

                float inertia = 0.99F;
                float gravity = 0.05F;
                if (this.isInWater()) {
                    for(int j = 0; j < 4; ++j) {
                        double f2 = 0.25;
                        this.level.addParticle(ParticleTypes.BUBBLE, newX - deltaX * f2, newY - deltaY * f2, newZ - deltaZ * f2, deltaX, deltaY, deltaZ);
                    }
                    inertia = 0.6F;
                }
                this.setDeltaMovement(deltaMovement.scale(inertia));

                deltaMovement = this.getDeltaMovement();
                this.setDeltaMovement(deltaMovement.x, deltaMovement.y - gravity, deltaMovement.z);

                this.setPos(newX, newY, newZ);

                BlockPos blockpos = this.blockPosition();
                BlockState blockstate = this.level.getBlockState(blockpos);
                if (!blockstate.isAir()) {
                    VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
                    if (!voxelshape.isEmpty()) {
                        Vec3 vec31 = this.position();

                        for(AABB aabb : voxelshape.toAabbs()) {
                            if (aabb.move(blockpos).contains(vec31)) {
                                this.isAttached = true;
                                break;
                            }
                        }
                    }
                }

                this.checkInsideBlocks();
            }
        }

    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport) {
    }

    public int retrieve(@Nonnull ItemStack fishingRod) {
        Player player = this.getPlayerOwner();
        if (!this.level.isClientSide && player != null && !this.shouldStopFishing(player)) {
            int rodDamage = 2;
            this.discard();
            return rodDamage;
        } else {
            return 0;
        }
    }

    private void breakRope(Player player) {
        // Play line break sound
        this.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LEASH_KNOT_BREAK, SoundSource.PLAYERS, 0.25F, 1.0F);
        this.discard();
    }

    private boolean shouldStopFishing(Player player) {
        ItemStack mainHandRod = player.getMainHandItem();
        ItemStack offhandRod = player.getOffhandItem();
        boolean flag = mainHandRod.canPerformAction(ToolActions.FISHING_ROD_CAST);
        boolean flag1 = offhandRod.canPerformAction(ToolActions.FISHING_ROD_CAST);

        if (!player.isRemoved() && player.isAlive() && (flag || flag1) && !(this.distanceToSqr(player) > RANGE_SQR)) {
            return false;
        } else {
            if (this.distanceToSqr(player) > RANGE_SQR) {
                this.breakRope(player);
            } else {
                this.discard();
            }
            return true;
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return false;
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        Vec3 deltaMovement = this.getDeltaMovement();

        BlockState blockState = level.getBlockState(result.getBlockPos());
        level.playSound(null, this.getX(), this.getY(), this.getZ(), blockState.getSoundType().getHitSound(), SoundSource.PLAYERS,1F, 1);

        // Bounce if hitting the side or bottom of a block
        Direction hitDirection = result.getDirection();
        if (!hitDirection.equals(Direction.UP)) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.1));
            if (hitDirection.getAxis() == Direction.Axis.X) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(-1,1,1));
            } else if (hitDirection.getAxis() == Direction.Axis.Z) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1,1,-1));
            }
        } else {
            Vec3 hitPos = result.getLocation();
            RandomSource random = level.getRandom();
            for (int particles = 3; particles > 0; particles--) {
                Vec3 reversedMovement = deltaMovement.reverse().add(random.nextDouble()-0.5, random.nextDouble()-0.5, random.nextDouble()-0.5).normalize().scale(0.05);
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), hitPos.x, hitPos.y, hitPos.z, reversedMovement.x, reversedMovement.y, reversedMovement.z);
            }

            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(hitPos);
            this.shakeTime = 7;
            this.isAttached = true;

            this.playSound(SoundEvents.CHAIN_HIT, 0.5F, 0.75F);

            Player player = this.getPlayerOwner();
            if (player != null){
                Vec3 playerPos = player.getEyePosition();
                Vec3 ropeVec = this.position().subtract(playerPos);

                // Pull back a little to hook nicely onto corners
                this.setPos(hitPos.subtract(ropeVec.normalize().scale(3F/16)));

                Vec3 soundPosition = playerPos.add(ropeVec.normalize().scale(2));
                level.playSound(null, soundPosition.x, soundPosition.y, soundPosition.z, RaspberrySoundEvents.GRAPPLING_HOOK_TIGHTEN.get(), SoundSource.PLAYERS,0.25F, 1);
            }
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        this.updateOwnerInfo(null);
        super.remove(reason);
    }

    @Override
    public void onClientRemoval() {
        this.updateOwnerInfo(null);
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        this.updateOwnerInfo(this);
    }

    private void updateOwnerInfo(@Nullable GrapplingHookEntity grapplingHook) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            ((PlayerWithGrapplingHook)player).raspberryCore$setHook(grapplingHook);
        }
    }

    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    @Nonnull
    public ItemStack getFishingLine() {
        return this.fishingLine;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    public void writeSpawnData(FriendlyByteBuf buffer) {
        Player player = this.getPlayerOwner();
        if (player != null) {
            buffer.writeUUID(player.getUUID());
        }

        buffer.writeInt(this.luck);
        buffer.writeItem(this.fishingLine);
        buffer.writeItem(this.bobber);
        buffer.writeItem(this.fishingRod);
    }

    public void readSpawnData(FriendlyByteBuf additionalData) {
    }

    @Nonnull
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Nonnull
    public EntityType<?> getType() {
        return RaspberryEntityTypes.GRAPPLING_HOOK.get();
    }


    protected void defineSynchedData() {
    }
}
