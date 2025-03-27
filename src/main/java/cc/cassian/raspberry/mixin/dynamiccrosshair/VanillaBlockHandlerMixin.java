package cc.cassian.raspberry.mixin.dynamiccrosshair;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.QuarkCompat;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.impl.CrosshairContextImpl;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.tag.ModTags;

@Pseudo
@Mixin(CrosshairContextImpl.class)
public abstract class VanillaBlockHandlerMixin implements CrosshairContext {

    @WrapOperation(
            method = "checkToolWithBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private static boolean fixQuarkGoldTools(Item instance, BlockState state, Operation<Boolean> original) {
        if (ModCompat.QUARK && instance instanceof DiggerItem diggerItem && diggerItem.getTier().equals(Tiers.GOLD)) {
            if (QuarkCompat.checkGold(diggerItem, state))
                return true;
        }
        return original.call(instance, state);
    }

    @Inject(method = "checkToolWithBlock", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void checkShearsTag(CallbackInfoReturnable<InteractionType> cir) {
        if (this.getItem() instanceof ShearsItem) {
            if (this.getBlockState().is(RaspberryTags.SHEARS_SHOULD_MINE))
                cir.setReturnValue(InteractionType.CORRECT_TOOL);
            else if (this.getBlockState().is(RaspberryTags.SHEARS_SHOULD_USE))
                cir.setReturnValue(InteractionType.USABLE_TOOL);
        }
        else if (this.getItem() instanceof KnifeItem) {
            if (this.getBlockState().is(RaspberryTags.KNIVES_SHOULD_USE))
                cir.setReturnValue(InteractionType.USABLE_TOOL);
            else if (this.getBlockState().is(ModTags.MINEABLE_WITH_KNIFE))
                cir.setReturnValue(InteractionType.CORRECT_TOOL);
        }
        else if (this.getItem() instanceof AxeItem && this.getBlockState().is(RaspberryTags.AXES_SHOULD_USE)) {
            cir.setReturnValue(InteractionType.USABLE_TOOL);
        }
        else if (this.getItem() instanceof HoeItem && this.getBlockState().is(RaspberryTags.HOES_SHOULD_USE)) {
            cir.setReturnValue(InteractionType.USABLE_TOOL);
        }
        }

}
