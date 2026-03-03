package cc.cassian.raspberry.mixin.caverns_and_chasms;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.CopperAgeBackportCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamabnormals.caverns_and_chasms.common.entity.animal.CopperGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInput;

@Mixin(CopperGolem.class)
public class CopperGolemMixin {
	@Inject(
			method = "createGolem", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;moveTo(DDDFF)V", remap = true))
	private static void raspberry$spawnABetterGolem(Level level, BlockPos pos, BlockState state, CallbackInfo ci, @Local LocalRef<LivingEntity> golem, @Local(name = "abovestate") BlockState aboveState, @Local(name = "yRot") float yRot) {
		if (ModConfig.get().betterCopperGolems && ModCompat.hasCopperAgeBackport()) {
			CopperAgeBackportCompat.spawnCopperGolem(level, pos, golem, yRot, aboveState);
		}
	}

}
