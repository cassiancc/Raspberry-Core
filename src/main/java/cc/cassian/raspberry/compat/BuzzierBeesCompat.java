package cc.cassian.raspberry.compat;

import com.teamabnormals.buzzier_bees.core.registry.BBBlocks;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BuzzierBeesCompat {
    public static @NotNull Block getSoulCandle() {
        return BBBlocks.SOUL_CANDLE.get();
    }
}
