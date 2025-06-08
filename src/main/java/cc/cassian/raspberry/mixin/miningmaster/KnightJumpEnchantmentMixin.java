package cc.cassian.raspberry.mixin.miningmaster;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.infernalstudios.miningmaster.enchantments.KnightJumpEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KnightJumpEnchantment.class)
public class KnightJumpEnchantmentMixin {
    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private static boolean disableDoubleJumpInWater(KeyMapping instance, Operation<Boolean> original, @Local Minecraft mc) {
        if (mc.player == null) return false;
        return original.call(instance) & !mc.player.isInWater();
    }
}
