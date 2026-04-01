package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.events.ChangeWeatherEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mehvahdjukaar.supplementaries.common.block.tiles.WindVaneBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

import static net.minecraft.util.Mth.lerp;

@Mixin(WindVaneBlockTile.class)
public abstract class WindVaneBlockTileMixin extends BlockEntity {

	@Shadow
	private float prevYaw;

	public WindVaneBlockTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@ModifyReturnValue(
			method = "getYaw",
			at = @At(value = "RETURN", target = "Lnet/minecraft/util/Mth;clamp(FFF)F")
			)
	private float shouldSpinWeatherVane(float original) {
		if (ChangeWeatherEvent.shouldSpin()) {
			if (getBlockPos().equals(ChangeWeatherEvent.changeWeatherPos))
				ChangeWeatherEvent.playParticle(getLevel(), getBlockPos());
			Random random = new Random();
			System.out.println(ChangeWeatherEvent.ticksUntilStopSpinning);
			if (ChangeWeatherEvent.ticksUntilStopSpinning >4) {
				return random.nextInt();
			}
			return lerp(0, prevYaw, random.nextFloat() * (prevYaw+8 - prevYaw-8) + prevYaw-8);
		}
		return original;
	}
}
