package cc.cassian.raspberry.mixin.controllable;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.controllable.EmiSupport;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.controllable.client.ControllerInput;
import com.mrcrayfish.controllable.client.gui.navigation.NavigationPoint;
import com.mrcrayfish.controllable.client.util.ClientHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ControllerInput.class)
public class ControllerInputMixin {
    @WrapOperation(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getSlotUnderMouse()Lnet/minecraft/world/inventory/Slot;"), remap = false
    )
    private Slot emiClientTick(AbstractContainerScreen instance, Operation<Slot> original, @Local(name = "finalCursorX") double finalCursorX, @Local(name="finalCursorY") double finalCursorY, @Local(name="dragX") double dragX, @Local(name="dragY") double dragY, @Local(name="activeMouseButton") int activeMouseButton) {
        if (ModCompat.EMI) {
            if (EmiSupport.invokeMouseDragged(activeMouseButton, finalCursorX, finalCursorY, dragX, dragY)) {
                return null;
            }
        }
        return original.call(instance);
    }

    @Inject(
            method = "gatherNavigationPoints",
            at = @At(value = "RETURN"), remap = false
    )
    private void gatherEmiNavigationPoints(Screen screen, @Coerce Object navigate, int mouseX, int mouseY, CallbackInfoReturnable<List<NavigationPoint>> cir) {
        if (ModCompat.EMI && ClientHelper.isPlayingGame()) {
            cir.getReturnValue().addAll(EmiSupport.getNavigationPoints(screen));
        }
    }

    @WrapOperation(
            method = "invokeMouseClick(Lnet/minecraft/client/gui/screens/Screen;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mrcrayfish/controllable/client/ControllerInput;invokeMouseClick(Lnet/minecraft/client/gui/screens/Screen;IDD)V"), remap = false
    )
    private void emiMouseClicked(ControllerInput instance, Screen screen, int button, double mouseX, double mouseY, Operation<Void> original) {
        if (!ModCompat.EMI || !EmiSupport.invokeMouseClick(button, mouseX, mouseY)) {
            original.call(instance, screen, button, mouseX, mouseY);
        }
    }

    @WrapOperation(
            method = "invokeMouseReleased(Lnet/minecraft/client/gui/screens/Screen;I)V",
            at = @At(value = "INVOKE", target = "Lcom/mrcrayfish/controllable/client/ControllerInput;invokeMouseReleased(Lnet/minecraft/client/gui/screens/Screen;IDD)V"), remap = false
    )
    private void emiMouseReleased(ControllerInput instance, Screen screen, int button, double mouseX, double mouseY, Operation<Void> original) {
        if (!ModCompat.EMI || !EmiSupport.invokeMouseReleased(button, mouseX, mouseY)) {
            original.call(instance, screen, button, mouseX, mouseY);
        }
    }
}
