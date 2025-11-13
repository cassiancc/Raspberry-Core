package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.Ashball;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SackBlockTile;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SafeBlockTile;
import net.mehvahdjukaar.supplementaries.common.utils.FlowerPotHandler;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SupplementariesCompat {
    public static boolean isStorage(BlockEntity be) {
        return be instanceof SackBlockTile || be instanceof SafeBlockTile;
    }

    public static void register() {
        DispenserBlock.registerBehavior(RaspberryItems.ASHBALL.get(), new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                return new Ashball(level, position.x(), position.y(), position.z());
            }
        });

        FlowerPotHandler.registerCustomFlower(RaspberryBlocks.CHEERFUL_WILDFLOWERS.getItemSupplier().get(), RaspberryMod.locate("block/cheery_wildflowers_potted"));
        FlowerPotHandler.registerCustomFlower(RaspberryBlocks.PINK_PETALS.getItemSupplier().get(), RaspberryMod.locate("block/playful_wildflowers_potted"));
        FlowerPotHandler.registerCustomFlower(RaspberryBlocks.MOODY_WILDFLOWERS.getItemSupplier().get(), RaspberryMod.locate("block/moody_wildflowers_potted"));
        FlowerPotHandler.registerCustomFlower(RaspberryBlocks.HOPEFUL_WILDFLOWERS.getItemSupplier().get(), RaspberryMod.locate("block/hopeful_wildflowers_potted"));
        FlowerPotHandler.registerCustomFlower(RaspberryBlocks.CLOVERS.getItemSupplier().get(), RaspberryMod.locate("block/clovers_potted"));
    }
}
