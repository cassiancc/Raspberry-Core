package cc.cassian.raspberry.mixin.quark;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.addons.oddities.block.be.CrateBlockEntity;

@Mixin(value = CrateBlockEntity.class, remap = false)
public abstract class CrateContainerBlacklistMixin {
    @Unique
    private static final TagKey<Item> raspberry$CRATE_BLACKLIST = ItemTags.create(new ResourceLocation("raspberry", "crate_blacklist"));

    @Inject(method = "canPlaceItemThroughFace",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void raspberry$preventBlacklistedItemsContainer(int index, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(raspberry$CRATE_BLACKLIST)) {
            cir.setReturnValue(false);
        }
    }
}