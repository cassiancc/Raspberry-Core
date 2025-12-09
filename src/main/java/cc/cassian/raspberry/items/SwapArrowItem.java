package cc.cassian.raspberry.items;

import cc.cassian.raspberry.entity.SwapArrowEntity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SwapArrowItem extends ArrowItem {
    public SwapArrowItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        return new SwapArrowEntity(worldIn, shooter);
    }
}
