package cc.cassian.raspberry.compat;

import com.teamabnormals.environmental.core.other.EnvironmentalProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EnvironmentalCompat {

    public static BlockBehaviour.Properties getTruffleProperties() {
        return EnvironmentalProperties.BURIED_TRUFFLE;
    }
}
