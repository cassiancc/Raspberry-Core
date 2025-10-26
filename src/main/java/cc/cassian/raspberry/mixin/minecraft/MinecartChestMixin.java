package cc.cassian.raspberry.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecartChest.class)
public class MinecartChestMixin {
    @ModifyReturnValue(method = "getDefaultDisplayBlockState", at = @At("RETURN"))
    public BlockState useBarrelBlockstate(BlockState original) {
        return Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP);
    }
}
