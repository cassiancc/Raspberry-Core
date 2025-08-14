package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.entity.Ashball;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SackBlockTile;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SafeBlockTile;
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
    }
}
