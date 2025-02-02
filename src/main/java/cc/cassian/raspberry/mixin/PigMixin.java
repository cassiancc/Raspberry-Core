package cc.cassian.raspberry.mixin;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public class PigMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    public void pigsEatFoodNotAir(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().equals(Items.AIR))
            cir.setReturnValue(false);
    }
}
