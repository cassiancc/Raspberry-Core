package cc.cassian.raspberry.mixin.dynamiccrosshair;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.QuarkCompat;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.handler.VanillaBlockHandler;
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
@Mixin(VanillaBlockHandler.class)
public class VanillaBlockHandlerMixin {

    @WrapOperation(
            method = "checkToolWithBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private static boolean fixQuarkGoldTools(Item instance, BlockState state, Operation<Boolean> original) {
        if (ModCompat.hasQuark() && instance instanceof DiggerItem diggerItem && diggerItem.getTier().equals(Tiers.GOLD)) {
            if (QuarkCompat.checkGold(diggerItem, state))
                return true;
        }
        return original.call(instance, state);
    }

    @Inject(method = "checkToolWithBlock", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void checkShearsTag(CrosshairContext context, CallbackInfoReturnable<Crosshair> cir) {
        if (context.getItem() instanceof ShearsItem) {
            if (context.getBlockState().is(RaspberryTags.SHEARS_SHOULD_MINE))
                cir.setReturnValue(Crosshair.CORRECT_TOOL);
            else if (context.getBlockState().is(RaspberryTags.SHEARS_SHOULD_USE))
                cir.setReturnValue(Crosshair.USABLE);
        }
        else if (context.getItem() instanceof KnifeItem) {
            if (context.getBlockState().is(RaspberryTags.KNIVES_SHOULD_USE))
                cir.setReturnValue(Crosshair.USABLE);
            else if (context.getBlockState().is(ModTags.MINEABLE_WITH_KNIFE))
                cir.setReturnValue(Crosshair.CORRECT_TOOL);
        }
        else if (context.getItem() instanceof AxeItem && context.getBlockState().is(RaspberryTags.AXES_SHOULD_USE)) {
            cir.setReturnValue(Crosshair.USABLE);
        }
        else if (context.getItem() instanceof HoeItem && context.getBlockState().is(RaspberryTags.HOES_SHOULD_USE)) {
            cir.setReturnValue(Crosshair.USABLE);
        }
        }

}
