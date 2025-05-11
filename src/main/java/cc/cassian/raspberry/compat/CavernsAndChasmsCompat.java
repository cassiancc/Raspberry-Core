package cc.cassian.raspberry.compat;

import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import com.teamabnormals.caverns_and_chasms.core.registry.CCItems;
import com.teamabnormals.caverns_and_chasms.core.registry.CCParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CavernsAndChasmsCompat {
    public static Item getDepthGauge() {
        return CCItems.DEPTH_GAUGE.get();
    }

    public static Item getBarometer() {
        return CCItems.BAROMETER.get();
    }

    public static Block getCupricCandle() {
        return CCBlocks.CUPRIC_CANDLE.get();
    }

    public static @NotNull SimpleParticleType getCupricCandleFlame() {
        return CCParticleTypes.CUPRIC_FIRE_FLAME.get();
    }
}
