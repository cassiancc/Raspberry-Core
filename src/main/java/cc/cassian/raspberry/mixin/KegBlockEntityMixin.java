package cc.cassian.raspberry.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;

@Pseudo
@Mixin(KegBlockEntity.class)
public class KegBlockEntityMixin {
    @Redirect(method = "canFerment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getCraftingRemainingItem()Lnet/minecraft/world/item/Item;"), remap = false)
    private Item mixin(Item instance) {
        if (instance == null) {
            instance = Items.AIR;
        }
        return instance;
    }
}
