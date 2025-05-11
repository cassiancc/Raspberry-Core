package cc.cassian.raspberry.compat;

import com.teamabnormals.buzzier_bees.core.registry.BBBlocks;
import net.minecraft.world.level.block.Block;

public class BuzzierBeesCompat {
    public static Block getSoulCandle() {
        return BBBlocks.SOUL_CANDLE.get();
    }
}
