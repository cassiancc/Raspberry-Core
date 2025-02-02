package cc.cassian.raspberry.mixin.upgrade_aquatic;

import com.teamabnormals.upgrade_aquatic.common.entity.animal.Pike;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(Pike.class)
public class PikeMixin {

    @Redirect(method = "positionRider", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/animal/Pike;getPassengers()Ljava/util/List;"))
    private List<Entity> mixin(Pike instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

}
