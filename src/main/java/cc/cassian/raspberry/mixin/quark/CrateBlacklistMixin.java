package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.compat.QuarkCompat;
import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStackHandler.class, remap = false)
public abstract class CrateBlacklistMixin {

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$preventBlacklistedItemsValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (QuarkCompat.isCrateItemHandler((ItemStackHandler) (Object) this) && stack.is(RaspberryTags.CRATE_BLACKLIST)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "insertItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$preventBlacklistedItemsInsert(int slot, ItemStack stack, boolean simulate,
                                                         CallbackInfoReturnable<ItemStack> cir) {
        if (QuarkCompat.isCrateItemHandler((ItemStackHandler) (Object) this) && stack.is(RaspberryTags.CRATE_BLACKLIST)) {
            cir.setReturnValue(stack);
        }
    }
}