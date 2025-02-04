package cc.cassian.raspberry.mixin.aquaculture;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.teammetallurgy.aquaculture.api.AquacultureAPI;
import com.teammetallurgy.aquaculture.block.blockentity.TackleBoxBlockEntity;
import com.teammetallurgy.aquaculture.item.HookItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(TackleBoxBlockEntity.class)
public class TackleBoxBlockEntityMixin {
    @Inject(method = "canBePutInTackleBox", at = @At("HEAD"), cancellable = true, remap = false)
    private static void baitUsedToBeBelievable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();
        boolean isDyeable = item instanceof DyeableLeatherItem;
        cir.setReturnValue(stack.is(AquacultureAPI.Tags.TACKLE_BOX) || item instanceof HookItem || stack.is(RaspberryTags.BAIT) || stack.is(AquacultureAPI.Tags.FISHING_LINE) && isDyeable || stack.is(AquacultureAPI.Tags.BOBBER) && isDyeable);
    }
}
