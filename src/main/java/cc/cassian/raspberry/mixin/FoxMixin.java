package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Fox.class)
public class FoxMixin {

    @WrapOperation(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z")
    )
    private boolean disableRabbitFootDrop(RandomSource instance, Operation<Boolean> original) {
        if (ModConfig.get().noRabbitFootDrops) {
            return false;
        }
        return original.call(instance);
    }

    @Inject(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z"),
            cancellable = true)
    private void disableRabbitDrops(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (ModConfig.get().foxes_noRabbitDrops) {
            ci.cancel();
        }
    }
}
