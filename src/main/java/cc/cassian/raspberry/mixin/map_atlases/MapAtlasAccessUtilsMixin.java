package cc.cassian.raspberry.mixin.map_atlases;

import cc.cassian.raspberry.overlay.OverlayHelpers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.mapatlases.MapAtlasesMod;
import pepjebs.mapatlases.utils.MapAtlasesAccessUtils;

@Mixin(MapAtlasesAccessUtils.class)
public class MapAtlasAccessUtilsMixin {
    @Inject(method = "getAtlasFromInventory", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void mixin(Inventory inventory, boolean onlyHotbar, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue().equals(ItemStack.EMPTY)) {
            cir.setReturnValue(OverlayHelpers.checkInventoryForStack(inventory, null, MapAtlasesMod.MAP_ATLAS.get()));
        }
    }
}
