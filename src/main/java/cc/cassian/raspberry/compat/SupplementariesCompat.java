package cc.cassian.raspberry.compat;

import net.mehvahdjukaar.supplementaries.common.block.tiles.SackBlockTile;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SafeBlockTile;
import net.minecraft.world.level.block.entity.BlockEntity;
import vectorwing.farmersdelight.common.block.entity.CabinetBlockEntity;

public class SupplementariesCompat {
    public static boolean isStorage(BlockEntity be) {
        return be instanceof SackBlockTile || be instanceof SafeBlockTile;
    }
}
