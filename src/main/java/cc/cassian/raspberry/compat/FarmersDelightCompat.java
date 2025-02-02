package cc.cassian.raspberry.compat;

import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.block.entity.CabinetBlockEntity;

public class FarmersDelightCompat {
    public static boolean isCabinet(BlockEntity be) {
        return be instanceof CabinetBlockEntity;
    }
}
