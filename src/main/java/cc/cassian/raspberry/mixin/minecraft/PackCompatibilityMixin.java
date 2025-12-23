package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackCompatibility;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PackCompatibility.class)
public class PackCompatibilityMixin {
	@WrapMethod(method = "isCompatible")
	private boolean setCompatible(Operation<Boolean> original) {
		if (ModConfig.get().compatibleDatapacks) {
			return true;
		} else {
			return original.call();
		}
	}
}
