package cc.cassian.raspberry.mixin.additional_enchantments;

import de.cadentem.additional_enchantments.capability.PlayerData;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerData.class)
public class PlayerDataMixin {
    @Shadow
    public HomingEnchantment.TypeFilter homingTypeFilter;

    @Shadow
    public HomingEnchantment.Priority homingPriority;

    @Inject(method = "cycleHomingFilter", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void forceHomingFilter(CallbackInfo ci) {
        this.homingTypeFilter = HomingEnchantment.TypeFilter.ANY;
        ci.cancel();
    }

    @Inject(method = "cycleHomingPriority", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void forceHomingPriority(CallbackInfo ci) {
        this.homingPriority = HomingEnchantment.Priority.CLOSEST;
        ci.cancel();
    }
}
