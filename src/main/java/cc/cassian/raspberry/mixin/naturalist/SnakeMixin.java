package cc.cassian.raspberry.mixin.naturalist;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.starfish_studios.naturalist.entity.Snake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(Snake.class)
public class SnakeMixin {

    @ModifyExpressionValue(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F")
    )
    private float disableRabbitFootDrop(float original) {
        if (original < 0.05F) {
            return 0.11F;
        } else return original;
    }
}
