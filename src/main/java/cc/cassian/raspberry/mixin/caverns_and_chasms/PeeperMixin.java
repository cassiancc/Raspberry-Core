package cc.cassian.raspberry.mixin.caverns_and_chasms;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.entity.monster.Peeper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static cc.cassian.raspberry.events.CreeperTickEvent.calculateNewSwell;

@Mixin(Peeper.class)
public abstract class PeeperMixin extends Mob {

    protected PeeperMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/teamabnormals/caverns_and_chasms/common/entity/monster/Peeper;getSwellDir()I"))
    private int getSwell(Peeper instance, Operation<Integer> original) {
        Integer swellDir = original.call(instance);
        return calculateNewSwell(instance, swellDir);
    }

}
