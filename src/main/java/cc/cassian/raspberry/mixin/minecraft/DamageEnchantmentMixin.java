package cc.cassian.raspberry.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static cc.cassian.raspberry.ModHelpers.getDamageBonus;

@Mixin(DamageEnchantment.class)
public abstract class DamageEnchantmentMixin {

    @ModifyReturnValue(method = "checkCompatibility", at = @At(value = "RETURN"))
    private boolean preventImpalingAndSharpness(boolean original, Enchantment other) {
        return original && !(other instanceof TridentImpalerEnchantment);
    }

    @WrapOperation(method = "doPostAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMobType()Lnet/minecraft/world/entity/MobType;"))
    private MobType arthropodTag(LivingEntity instance, Operation<MobType> original) {
        return getDamageBonus((DamageEnchantment)(Object) this, instance, original);
    }
}