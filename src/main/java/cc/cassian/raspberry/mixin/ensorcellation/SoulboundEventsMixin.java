package cc.cassian.raspberry.mixin.ensorcellation;

import cofh.ensorcellation.event.SoulboundEvents;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulboundEvents.class)
public class SoulboundEventsMixin {
	@Inject(
			method = "handlePlayerCloneEvent", remap = false, at = @At(value = "HEAD"), cancellable = true)
	private static void raspberry$disableCuring(PlayerEvent.Clone event, CallbackInfo ci) {
		ci.cancel();
	}
}
