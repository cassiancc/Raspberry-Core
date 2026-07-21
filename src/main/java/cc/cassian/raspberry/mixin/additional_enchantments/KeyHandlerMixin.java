package cc.cassian.raspberry.mixin.additional_enchantments;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.cadentem.additional_enchantments.client.KeyHandler;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(KeyHandler.class)
public class KeyHandlerMixin {
    @WrapMethod(
            method = "handleKey",
            remap = false
    )
    private static void disableKeyHandling(InputEvent.Key event, Operation<Void> original) {
        // Don't handle any key events
    }
}
