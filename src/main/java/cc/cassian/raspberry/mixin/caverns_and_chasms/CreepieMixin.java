package cc.cassian.raspberry.mixin.caverns_and_chasms;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.entity.monster.Peeper;
import com.teamabnormals.savage_and_ravage.common.entity.monster.Creepie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static cc.cassian.raspberry.events.CreeperTickEvent.calculateNewSwell;

@Mixin(Creepie.class)
public abstract class CreepieMixin extends Mob {

    protected CreepieMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/teamabnormals/savage_and_ravage/common/entity/monster/Creepie;getCreeperState()I"))
    private int getSwell(Creepie instance, Operation<Integer> original) {
        Integer swellDir = original.call(instance);
        return calculateNewSwell(instance, swellDir);
    }

}
