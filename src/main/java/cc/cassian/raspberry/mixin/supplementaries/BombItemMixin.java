package cc.cassian.raspberry.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.entities.BombEntity;
import net.mehvahdjukaar.supplementaries.common.items.BombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BombItem.class)
public class BombItemMixin {
    @Shadow @Final private BombEntity.BombType type;

    @Inject(
            method = "getRarity",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void rewriteRarity(ItemStack stack, CallbackInfoReturnable<Rarity> cir) {
        if (this.type == BombEntity.BombType.BLUE)
            cir.setReturnValue(Rarity.RARE);
        else
            cir.setReturnValue(Rarity.COMMON);
    }
}
