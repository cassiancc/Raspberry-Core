package cc.cassian.raspberry.mixin.naturalist;

import cc.cassian.raspberry.config.ModConfig;
import com.starfish_studios.naturalist.common.entity.Caterpillar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Caterpillar.class)
public class CaterpillarMixin {

    @Inject(method = "saveToHandTag", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void stackableButterflies(ItemStack stack, CallbackInfo ci) {
        if (ModConfig.get().naturalist_stackableItems)
            ci.cancel();
    }

}
