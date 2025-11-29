package cc.cassian.raspberry.mixin.cofh_core;

import cofh.core.init.CoreEnchantments;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CoreEnchantments.Types.class)
public class CoreEnchantmentsMixin {
    @Inject(method = "lambda$register$3", remap = false, at = @At(value = "HEAD"), cancellable = true)
    private static void mixin(Item item, CallbackInfoReturnable<Boolean> cir) {
        var stack = item.getDefaultInstance();
        if (stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES)) cir.setReturnValue(true);
    }
}
