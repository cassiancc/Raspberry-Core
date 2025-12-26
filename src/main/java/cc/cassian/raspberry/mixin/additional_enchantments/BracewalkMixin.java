package cc.cassian.raspberry.mixin.additional_enchantments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.enchantments.Bracewalk;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Bracewalk.class)
public class BracewalkMixin {
	@WrapOperation(method = "breakBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
	private static boolean spectatorsDontBreakCowbebs(Level instance, Operation<Boolean> original, @Local LivingEntity player) {
		return original.call(instance) || player.isSpectator();
	}
}
