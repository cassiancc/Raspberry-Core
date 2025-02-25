package cc.cassian.raspberry.mixin.vc_gliders;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.venturecraft.gliders.util.GliderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GliderUtil.class)
public class GliderUtilMixin {
    @WrapOperation(
            method = "lightningLogic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRainingAt(Lnet/minecraft/core/BlockPos;)Z")
    )
    private static boolean disableLightningStrikes(Level instance, BlockPos blockPos, Operation<Boolean> original) {
        if (ModConfig.get().gliders_disableLightning) {
            return false;
        } else {
            return original.call(instance, blockPos);
        }
    }

    @WrapOperation(
            method = "handleNetherLogic", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/venturecraft/gliders/common/item/GliderItem;hasNetherUpgrade(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean disableNetherDamage(ItemStack itemStack, Operation<Boolean> original) {
        if (ModConfig.get().gliders_disableNetherDamage) {
            return true;
        } else {
            return original.call(itemStack);
        }
    }

    @WrapOperation(
            method = "onTickPlayerGlide", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/venturecraft/gliders/common/item/GliderItem;hasNetherUpgrade(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private static boolean disableGlideNetherDamage(ItemStack itemStack, Operation<Boolean> original) {
        if (ModConfig.get().gliders_disableNetherDamage) {
            return true;
        } else {
            return original.call(itemStack);
        }
    }
}
