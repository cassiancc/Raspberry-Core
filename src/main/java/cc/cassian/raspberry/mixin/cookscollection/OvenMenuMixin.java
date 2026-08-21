package cc.cassian.raspberry.mixin.cookscollection;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.baisylia.cookscollection.block.ModBlocks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public abstract class OvenMenuMixin {

	@ModifyReturnValue(method = "stillValid(Lnet/minecraft/world/inventory/ContainerLevelAccess;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;)Z", at = @At(value = "RETURN"))
	private static boolean forceAllowOvens(boolean original, ContainerLevelAccess access, Player player, Block targetBlock) {
		if (!original && targetBlock.equals(ModBlocks.OVEN.get())) {
			return access.evaluate((level, pos) -> {
				if (!level.getBlockState(pos).is(RaspberryBlocks.SILT_OVEN.getBlock()) && !level.getBlockState(pos).is(RaspberryBlocks.ASH_OVEN.getBlock())) return false;
				return player.distanceToSqr((double) pos.getX() + (double) 0.5F, (double) pos.getY() + (double) 0.5F, (double) pos.getZ() + (double) 0.5F) <= (double) 64.0F;
			}, true);
		}
		return original;
	}
}
