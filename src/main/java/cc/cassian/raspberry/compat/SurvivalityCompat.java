package cc.cassian.raspberry.compat;

import com.github.creoii.survivality.config.SurvivalityConfig;
import com.github.creoii.survivality.util.SurvivalityUtils;
import net.minecraft.world.entity.player.Player;

public class SurvivalityCompat {
    public static boolean swappingEnabled() {
        return SurvivalityConfig.GENERAL.armorSwapping.get();
    }

    public static void swap(Player user) {
        SurvivalityUtils.swapArmor(user);
    }
}
