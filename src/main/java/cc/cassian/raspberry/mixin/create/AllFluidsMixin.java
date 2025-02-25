package cc.cassian.raspberry.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.simibubi.create.AllFluids.HONEY;

@Pseudo
@Mixin(AllFluids.class)
public class AllFluidsMixin {
    @WrapOperation(method = "registerFluidInteractions", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidInteractionRegistry;addInteraction(Lnet/minecraftforge/fluids/FluidType;Lnet/minecraftforge/fluids/FluidInteractionRegistry$InteractionInformation;)V", ordinal = 0), remap = false)
    private static void hotHoney(FluidType source, FluidInteractionRegistry.InteractionInformation interaction, Operation<Void> original) {
        FluidInteractionRegistry.addInteraction(ForgeMod.LAVA_TYPE.get(), new FluidInteractionRegistry.InteractionInformation(
                HONEY.get().getFluidType(),
                fluidState -> {
                    if (fluidState.isSource()) {
                        return Blocks.OBSIDIAN.defaultBlockState();
                    } else {
                        return AllPaletteStoneTypes.OCHRUM.getBaseBlock()
                                .get()
                                .defaultBlockState();
                    }
                }
        ));
    }

    @Inject(method = "getLavaInteraction", at = @At("RETURN"), cancellable = true, remap = false)
    private static void whatDoesThisMethodDoExactly(FluidState fluidState, CallbackInfoReturnable<BlockState> cir) {
        Fluid fluid = fluidState.getType();
        if (fluid.isSame(HONEY.get()))
            cir.setReturnValue(AllPaletteStoneTypes.OCHRUM.getBaseBlock()
                    .get()
                    .defaultBlockState());
    }
}
