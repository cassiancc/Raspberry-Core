package cc.cassian.raspberry.items;

import cc.cassian.raspberry.entity.SwapArrowEntity;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class SwapArrowItem extends ArrowItem {
    public SwapArrowItem(Item.Properties builder) {
        super(builder);
        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            // Copy of execute function
            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.getLevel();
                Position position = DispenserBlock.getDispensePosition(source);
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                Projectile projectile = this.getProjectile(level, position, stack);

                // Added dispenser position so it can swap position with hit entity
                projectile.getPersistentData().putLong("DispenserSourcePosition",source.getPos().asLong());

                projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
                level.addFreshEntity(projectile);
                stack.shrink(1);
                return stack;
            }
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                SwapArrowEntity swap_arrow = new SwapArrowEntity(level, position.x(), position.y(), position.z());
                swap_arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return swap_arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        return new SwapArrowEntity(worldIn, shooter);
    }
}
