package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.events.ChangeWeatherEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mehvahdjukaar.supplementaries.common.block.tiles.WindVaneBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

import static net.minecraft.util.Mth.lerp;

@Mixin(WindVaneBlockTile.class)
public abstract class WindVaneBlockTileMixin {

	@WrapOperation(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F")
			)
	private static float shouldSpinWeatherVane(float value, float min, float max, Operation<Float> original, Level pLevel) {
		if (ChangeWeatherEvent.shouldSpin())
			return lerp(0, min, new Random().nextFloat() * (max - min) + min);
		return original.call(value, min, max);
	}
}
