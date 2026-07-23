package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModHelpers;
import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static cc.cassian.raspberry.ModHelpers.getDamageBonus;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin {
    @ModifyVariable(method = "onHitEntity", at = @At("STORE"), ordinal = 0)
    private float setDefaultDamage(float value) {
        return ModConfig.get().thrown_trident_base_damage;
    }

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMobType()Lnet/minecraft/world/entity/MobType;"))
    private MobType mixin(LivingEntity instance, Operation<MobType> original) {
        return getDamageBonus(ModHelpers.getDamageEnchantment(instance.getMainHandItem()), instance, original);
    }
}
