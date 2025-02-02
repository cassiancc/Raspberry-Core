package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelSummary.class)
public abstract class LevelSummaryMixin {

	@Inject(method = "createInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelSummary;markVersionInList()Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void hideCreateInfo(CallbackInfoReturnable<MutableComponent> cir, MutableComponent mutablecomponent) {
		if (ModConfig.get().hideWorldVersion)
			cir.setReturnValue(mutablecomponent);
	}
}
