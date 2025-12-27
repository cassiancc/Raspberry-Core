package cc.cassian.raspberry.mixin.alloyed;

import com.molybdenum.alloyed.common.registry.ModBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static cc.cassian.raspberry.RaspberryMod.identifier;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At(value = "RETURN"), cancellable = true)
    private void forceAllowAlloyed(BlockState arg, CallbackInfoReturnable<Boolean> cir) {
        if (arg.getBlock() instanceof EncasedCogwheelBlock && (arg.is(ModBlocks.STEEL_ENCASED_COGWHEEL.get()) || arg.is(ModBlocks.STEEL_ENCASED_LARGE_COGWHEEL.get())) || arg.getBlock() instanceof EncasedShaftBlock && arg.is(ModBlocks.STEEL_ENCASED_SHAFT.get())) {
            cir.setReturnValue(true);
        }
    }
}
