package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.CompassOverlay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Inject(method = "dropContents", at = @At(value = "RETURN"))
    private static void mixin(ItemStack stack, Player player, CallbackInfoReturnable<Boolean> cir) {
        CompassOverlay.checkInventoryForItems(player);
    }
}
