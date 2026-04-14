package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownTrident.class)
public class ThrownTridentMixin {
    @ModifyVariable(method = "onHitEntity", at = @At(value = "STORE", ordinal = 0), name = "f")
    private float setDefaultDamage(float value) {
        return ModConfig.get().thrown_trident_base_damage;
    }
}
