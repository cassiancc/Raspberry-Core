package cc.cassian.raspberry.mixin.peeping_angels;

import com.davigj.peeping_angels.core.mixin.CreeperAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.entity.monster.Peeper;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static cc.cassian.raspberry.events.CreeperTickEvent.calculateNewSwell;

// Peeping Angels overwrites Peeper#tick (for... some reason)
// Priority must be above Peeping Angels' (1000) to inject into the overwrite
@Mixin(value = Peeper.class, priority = 1500)
public abstract class PeeperMixin {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/davigj/peeping_angels/core/mixin/CreeperAccessor;setSwell(I)V", ordinal = 0, remap = false))
    private void raspberry$preventMidAirSwell(CreeperAccessor instance, int value, Operation<Void> original) {
        int swell = instance.getSwell();
        original.call(instance, swell + calculateNewSwell((Mob) (Object) this, value - swell));
    }

}
