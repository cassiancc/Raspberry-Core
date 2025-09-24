package cc.cassian.raspberry.compat.supplementaries;

import cc.cassian.raspberry.config.ModConfig;
import com.google.common.collect.Sets;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.supplementaries.common.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.common.misc.explosion.BombExplosion;
import net.mehvahdjukaar.supplementaries.common.network.ClientBoundSendKnockbackPacket;
import net.mehvahdjukaar.supplementaries.common.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.integration.CompatHandler;
import net.mehvahdjukaar.supplementaries.integration.FlanCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RoseGoldBombExplosion extends BombExplosion {
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    private final float radius;

    public RoseGoldBombExplosion(Level world, @Nullable Entity entity, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float radius, BombEntity.BombType bombType, BlockInteraction interaction) {
        super(world, entity, context, x, y, z, radius, bombType, interaction);
        this.level = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    @Override
    public void explode() {
        Set<BlockPos> set;
        Player var10000;
        label154: {
            this.level.gameEvent(this.getExploder(), GameEvent.EXPLODE, new BlockPos(this.x, this.y, this.z));
            set = Sets.newHashSet();
            Entity var5 = this.getExploder();
            if (var5 instanceof Projectile pr) {
                var5 = pr.getOwner();
                if (var5 instanceof Player pl) {
                    var10000 = pl;
                    break label154;
                }
            }

            var10000 = null;
        }

        Player owner = var10000;

        this.getToBlow().addAll(set);
        float diameter = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double)diameter - (double)1.0F);
        int l1 = Mth.floor(this.x + (double)diameter + (double)1.0F);
        int i2 = Mth.floor(this.y - (double)diameter - (double)1.0F);
        int i1 = Mth.floor(this.y + (double)diameter + (double)1.0F);
        int j2 = Mth.floor(this.z - (double)diameter - (double)1.0F);
        int j1 = Mth.floor(this.z + (double)diameter + (double)1.0F);
        List<Entity> list = this.level.getEntities(this.getExploder(), new AABB(k1, i2, j2, l1, i1, j1));
        ForgeHelper.onExplosionDetonate(this.level, this, list, diameter);
        Vec3 vector3d = new Vec3(this.x, this.y, this.z);

        for(Entity entity : list) {
            if (!entity.ignoreExplosion() && (owner == null || !CompatHandler.FLAN || FlanCompat.canAttack(owner, entity))) {
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
                        double visiblePercent = getSeenPercent(vector3d, entity);
                        double knockbackBeforeEnchantments = ((double)1.0F - normalizedDist) * visiblePercent* ModConfig.get().rose_gold_bomb_knockback;
                        double knockback = knockbackBeforeEnchantments;
                        boolean isPlayer = entity instanceof Player;
                        Player playerEntity = null;
                        if (isPlayer) {
                            playerEntity = (Player)entity;
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                this.getHitPlayers().put(playerEntity, new Vec3(dx * knockbackBeforeEnchantments, dy * knockbackBeforeEnchantments, dz * knockbackBeforeEnchantments));
                            }
                        }

                        if (entity instanceof LivingEntity) {
                            knockback = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, knockbackBeforeEnchantments);
                        }

                        entity.setDeltaMovement(entity.getDeltaMovement().add(dx * knockback, dy * knockback, dz * knockback));
                    }
                }
            }
        }

        if (!this.level.isClientSide) {
            for(Map.Entry<Player, Vec3> e : this.getHitPlayers().entrySet()) {
                NetworkHandler.CHANNEL.sendToClientPlayer((ServerPlayer)e.getKey(), new ClientBoundSendKnockbackPacket(e.getValue(), e.getKey().getId()));
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