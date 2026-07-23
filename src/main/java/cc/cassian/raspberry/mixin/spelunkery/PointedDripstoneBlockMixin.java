package cc.cassian.raspberry.mixin.spelunkery;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.events.DripstoneEvent;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.ordana.spelunkery.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;findTip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"))
    private static void soSalty(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 0) BlockPos blockPos) {
        if (ModCompat.SPELUNKERY) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, SpelunkeryCompat.rockSalt.defaultBlockState(), RaspberryTags.CONVERTS_TO_SALT, Fluids.WATER, SpelunkeryCompat.rockSalt, false);
        }
        if (ModCompat.QUARK) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("quark", "blue_corundum_cluster")).defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP), BlockTags.DIAMOND_ORES, Fluids.LAVA, ModBlocks.ROUGH_DIAMOND_BLOCK.get(), true);
        }
    }

}
