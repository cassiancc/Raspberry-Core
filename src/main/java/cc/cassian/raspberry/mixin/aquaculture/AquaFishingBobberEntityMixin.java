package cc.cassian.raspberry.mixin.aquaculture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teammetallurgy.aquaculture.entity.AquaFishingBobberEntity;
import com.teammetallurgy.aquaculture.init.AquaSounds;
import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(AquaFishingBobberEntity.class)
public class AquaFishingBobberEntityMixin {
    @Shadow @Final private ItemStack fishingRod;

    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;getStackInSlot(I)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    private void shrinkBait(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ItemStackHandler rodHandler = AquaFishingRodItem.getHandler(this.fishingRod);
        AquaFishingBobberEntity bobber = (AquaFishingBobberEntity) (Object) this;
        ItemStack bait = rodHandler.getStackInSlot(1);
        if (!bait.isEmpty()) {
            if (bait.getCount() == 1) {
                bobber.playSound((SoundEvent)AquaSounds.BOBBER_BAIT_BREAK.get(), 0.7F, 0.2F);
            }
            bait.shrink(1);
            rodHandler.setStackInSlot(1, bait);
        }
    }

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemStackHandler;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private void stopResettingBaitStackPlz(ItemStackHandler instance, int slot, ItemStack stack, Operation<Void> original, @Local Player angler, @Local ItemStackHandler handler) {
        // Just in case
    }
}
