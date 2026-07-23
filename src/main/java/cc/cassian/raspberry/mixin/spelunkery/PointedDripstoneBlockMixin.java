package cc.cassian.raspberry.mixin.spelunkery;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.events.DripstoneEvent;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.sugar.Local;
import com.ordana.spelunkery.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;findTip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"))
    private static void soSalty(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 0) BlockPos blockPos) {
        if (ModCompat.SPELUNKERY && ModConfig.get().saltGenerator) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, SpelunkeryCompat.rockSalt.defaultBlockState(), RaspberryTags.CONVERTS_TO_SALT, Fluids.WATER, SpelunkeryCompat.rockSalt, false);
        }
        if (ModCompat.QUARK && ModConfig.get().corundumGenerators) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, raspberryCore$getCluster("blue"), BlockTags.DIAMOND_ORES, Fluids.LAVA, ModBlocks.ROUGH_DIAMOND_BLOCK.get(), true); // diamond

            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, raspberryCore$getCluster("indigo"), BlockTags.EMERALD_ORES, Fluids.LAVA, ModBlocks.ROUGH_EMERALD_BLOCK.get(), true); // amethyst

            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, raspberryCore$getCluster("white"), Tags.Blocks.ORES_QUARTZ, Fluids.LAVA, ModBlocks.ROUGH_QUARTZ_BLOCK.get(), true); // quartz

            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, raspberryCore$getCluster("red"), Tags.Blocks.ORES_REDSTONE, Fluids.LAVA, ModBlocks.ROUGH_CINNABAR_BLOCK.get(), true); // redstone
        }
    }

    @Unique
	private static @Nullable BlockState raspberryCore$getCluster(String colour) {
        Block cluster = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("quark", colour + "_corundum_cluster"));
        if (cluster == null) return null;
        return cluster.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
    }

}
