package cc.cassian.raspberry.compat;

import com.teamabnormals.caverns_and_chasms.core.registry.CCItems;
import net.minecraft.world.item.Item;

public class CavernsAndChasmsCompat {
    public static Item getDepthGauge() {
        return CCItems.DEPTH_GAUGE.get();
    }
}
