package cc.cassian.raspberry.items;

import cc.cassian.raspberry.entity.Ashball;
import cc.cassian.raspberry.entity.RoseGoldBombEntity;
import cc.cassian.raspberry.entity.SwapArrowEntity;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import net.mehvahdjukaar.supplementaries.common.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.common.items.BombItem;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class RoseGoldBombItem extends Item {
    public RoseGoldBombItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {

            @Override
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                return new RoseGoldBombEntity(level, position.x(), position.y(), position.z());
            }

            protected float getUncertainty() {
                return 11.0F;
            }

            protected float getPower() {
                return 1.3F;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                RaspberrySoundEvents.ROSE_GOLD_BOMB_THROW.get(),
                SoundSource.NEUTRAL,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!level.isClientSide) {
            RoseGoldBombEntity ashball = new RoseGoldBombEntity(level, player);
            ashball.setItem(itemStack);
            ashball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(ashball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
