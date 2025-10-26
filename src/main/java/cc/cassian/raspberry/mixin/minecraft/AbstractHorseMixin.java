package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Mob {
    @Inject(
            method = "isFood",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void bypassExpensiveCalculationIfNecessary(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(RaspberryTags.HORSE_FOOD)) {
            cir.setReturnValue(true);
        }
    }

    protected AbstractHorseMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isSaddled();

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Animal.travel (Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 disableWandering(Vec3 input) {
        if (ModConfig.get().horses_noWander && isSaddled() && this.getLeashHolder() == null && !this.isVehicle())
            return(Vec3.ZERO);
        return input;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 1.0f))
    private float horseHigherStepHeight(float value){
        if (ModConfig.get().horses_stepHeight) {
            return 1.1f;
        }
        return value;
    }

    @Shadow
    private boolean allowStandSliding;
    @Shadow public abstract boolean isTamed();
    @Shadow public abstract LivingEntity getControllingPassenger();

    @ModifyReturnValue(method = "isStanding", at = @At("RETURN"))
    private boolean horseNoBucking(boolean original) {
        if (ModConfig.get().horses_noBuck && allowStandSliding && isTamed() && getControllingPassenger() != null) {
            return false;
        }
        return original;
    }
}
