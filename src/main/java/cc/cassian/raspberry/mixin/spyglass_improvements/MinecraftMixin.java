package cc.cassian.raspberry.mixin.spyglass_improvements;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @WrapOperation(
            method = "handleKeybinds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z", ordinal=2)
    )
    private boolean handleSpyglassInput(KeyMapping instance, Operation<Boolean> original) {
        if (instance.isDown() || SpyglassImprovementsClient.useSpyglass.isDown()) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}
