package cc.cassian.raspberry.compat;

import com.starfish_studios.another_furniture.block.entity.DrawerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AnotherFurnitureCompat {
    public static boolean isDrawer(BlockEntity be) {
        return be instanceof DrawerBlockEntity;
    }
}
