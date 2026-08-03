package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.events.AftershockEvent;
import cc.cassian.raspberry.items.MarshmallowOnAStickItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (ModCompat.COFH_CORE)
            AftershockEvent.resist((LivingEntity) (Object) this);
    }

    @WrapOperation(
            method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEdible()Z"))
    private boolean customDrinkingSounds(ItemStack itemStack, Operation<Boolean> original) {
        if (itemStack.getItem() instanceof MarshmallowOnAStickItem) {
            var entity = (LivingEntity) (Object) this;
            if (MarshmallowOnAStickItem.canCookOrIsCooking(entity, itemStack)) {
                return false;
            }
        }
        return original.call(itemStack);
    }
}
