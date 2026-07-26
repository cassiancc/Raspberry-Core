package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
	@WrapOperation(
			method = "containerTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
	private boolean mixin(MultiPlayerGameMode instance, Operation<Boolean> original) {
		if (ModConfig.get().disableCreativeInventory)
			return false;
		return original.call(instance);
	}

	@WrapOperation(
			method = "init",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
	private boolean mixin2(MultiPlayerGameMode instance, Operation<Boolean> original) {
		if (ModConfig.get().disableCreativeInventory)
			return false;
		return original.call(instance);
	}
}
