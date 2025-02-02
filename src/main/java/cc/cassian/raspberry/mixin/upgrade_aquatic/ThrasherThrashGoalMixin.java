package cc.cassian.raspberry.mixin.upgrade_aquatic;

import com.teamabnormals.upgrade_aquatic.common.entity.ai.goal.thrasher.ThrasherThrashGoal;
import com.teamabnormals.upgrade_aquatic.common.entity.monster.Thrasher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(ThrasherThrashGoal.class)
public class ThrasherThrashGoalMixin {

    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;", ordinal = 0))
    public List<Entity> canUse(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

    @Redirect(method = "canUse", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;", ordinal = 1))
    public List<Entity> canUseTwo(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

    @Redirect(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;", ordinal = 0))
    public List<Entity> canContinueToUseMixin(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

    @Redirect(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;", ordinal = 1))
    public List<Entity> canContinueToUse2Mixin(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

}
