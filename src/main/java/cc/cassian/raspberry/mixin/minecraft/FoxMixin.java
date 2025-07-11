package cc.cassian.raspberry.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Fox.class)
public class FoxMixin {

    @WrapOperation(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextBoolean()Z")
    )
    private boolean disableRabbitFootDrop(RandomSource instance, Operation<Boolean> original) {
        return false;
    }
}
