package cc.cassian.raspberry.mixin.caverns_and_chasms;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.CopperAgeBackportCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamabnormals.caverns_and_chasms.common.entity.animal.CopperGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CopperGolem.class)
public class CopperGolemMixin {
	@Inject(
			method = "createGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveTo(DDDFF)V"))
	private static void raspberry$spawnABetterGolem(Level level, BlockPos pos, BlockState state, CallbackInfo ci, @Local LocalRef<LivingEntity> golem) {
		if (ModConfig.get().betterCopperGolems && ModCompat.hasCopperAgeBackport()) {
			CopperAgeBackportCompat.spawnCopperGolem(level, pos, golem, state);
		}
	}

}
