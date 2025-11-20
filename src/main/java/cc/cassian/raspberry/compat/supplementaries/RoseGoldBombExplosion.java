package cc.cassian.raspberry.compat.supplementaries;

import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.supplementaries.common.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.common.misc.explosion.BombExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class RoseGoldBombExplosion extends BombExplosion {
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    private final float radius;

    public RoseGoldBombExplosion(Level world, @Nullable Entity entity, @Nullable ExplosionDamageCalculator context, double x, double y, double z, BombEntity.BombType bombType, Explosion.BlockInteraction interaction) {
        super(world, entity, context, x, y, z, bombType, interaction);
        this.level = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = 4;
    }

    @Override
    public void explode() {
        this.level.gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, BlockPos.containing(this.x, this.y, this.z));
        float diameter = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double)diameter - (double)1.0F);
        int l1 = Mth.floor(this.x + (double)diameter + (double)1.0F);
        int i2 = Mth.floor(this.y - (double)diameter - (double)1.0F);
        int i1 = Mth.floor(this.y + (double)diameter + (double)1.0F);
        int j2 = Mth.floor(this.z - (double)diameter - (double)1.0F);
        int j1 = Mth.floor(this.z + (double)diameter + (double)1.0F);
        List<Entity> list = this.level.getEntities(this.getDirectSourceEntity(), new AABB(k1, i2, j2, l1, i1, j1));
        ForgeHelper.onExplosionDetonate(this.level, this, list, diameter);
        Vec3 vector3d = new Vec3(this.x, this.y, this.z);

        for(Entity entity : list) {
            if (!entity.ignoreExplosion()) {
                double distSq = entity.distanceToSqr(vector3d);
                double normalizedDist = Mth.sqrt((float)distSq) / diameter;
                if (normalizedDist <= (double)1.0F) {
                    double dx = entity.getX() - this.x;
                    double dy = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double dz = entity.getZ() - this.z;
                    double distFromCenterSqr = Mth.sqrt((float)(dx * dx + dy * dy + dz * dz));
                    if (distFromCenterSqr != (double)0.0F) {
                        dx /= distFromCenterSqr;
                        dy /= distFromCenterSqr;
                        dz /= distFromCenterSqr;
                        double d14 = getSeenPercent(vector3d, entity);
                        double d10 = ((double)1.0F - normalizedDist) * d14;
                        entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / (double)2.0F * (double)7.0F * (double)diameter + (double)1.0F)));
                        double d11 = d10;
                        boolean isPlayer = entity instanceof Player;
                        Player playerEntity;
                        if (isPlayer) {
                            playerEntity = (Player)entity;
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                this.getHitPlayers().put(playerEntity, new Vec3(dx * d10, dy * d10, dz * d10));
                            }
                        }

                        if (entity instanceof LivingEntity) {
                            if (entity instanceof Creeper creeper) {
                                creeper.ignite();
                            }

                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, d10);
                        }

                        entity.setDeltaMovement(entity.getDeltaMovement().add(dx * d11, dy * d11, dz * d11));
                    }
                }
            }
        }
    }

    public static class RoseGoldBombExplosionDamageCalculator extends ExplosionDamageCalculator {

        public RoseGoldBombExplosionDamageCalculator() {
        }

        public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}