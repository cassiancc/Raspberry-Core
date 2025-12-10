package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.common.api.leash.Leashable;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathfinderMob.class)
public abstract class LeashBehaviorMixin extends Mob implements Leashable {
    @Unique private double angularMomentum;

    protected LeashBehaviorMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public double angularMomentum() {
        return this.angularMomentum;
    }

    @Override
    public void setAngularMomentum(double angularMomentum) {
        this.angularMomentum = angularMomentum;
    }

    @Inject(method = "tickLeash", at = @At("HEAD"), cancellable = true)
    private void raspberry$onTickLeash(CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) {
                return;
        }

        ci.cancel();
        super.tickLeash();
        Leashable.tickLeash(this);
    }
}