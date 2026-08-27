package cc.cassian.raspberry.mixin.dungeons_mobs;

import com.infamous.dungeons_mobs.entities.creepers.IcyCreeperEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static cc.cassian.raspberry.events.CreeperTickEvent.calculateNewSwell;

@Mixin(IcyCreeperEntity.class)
public abstract class IcyCreeperMixin extends Mob {

    protected IcyCreeperMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/infamous/dungeons_mobs/entities/creepers/IcyCreeperEntity;getSwellDir()I"))
    private int getSwell(IcyCreeperEntity instance, Operation<Integer> original) {
        Integer swellDir = original.call(instance);
        return calculateNewSwell(instance, swellDir);
    }

}
