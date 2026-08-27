package cc.cassian.raspberry.events;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Mob;

public class CreeperTickEvent {
	public static Integer calculateNewSwell(Mob instance, Integer swellDir) {
		if (ModConfig.get().creepersDoNotExplodeInMidAir && !instance.isOnGround() && swellDir>0) {
			swellDir = -swellDir;
		}
		return swellDir;
	}
}
