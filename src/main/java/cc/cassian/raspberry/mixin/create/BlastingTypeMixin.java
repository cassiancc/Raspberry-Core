package cc.cassian.raspberry.mixin.create;

import cc.cassian.raspberry.config.ModConfig;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(AllFanProcessingTypes.BlastingType.class)
public class BlastingTypeMixin {
    @Inject(method = "process", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void mixin(ItemStack stack, Level level, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (cir.getReturnValue().equals(Collections.emptyList()) && ModConfig.get().create_blastproofing) {
            cir.setReturnValue(Collections.singletonList(stack));
        }
    }
}
