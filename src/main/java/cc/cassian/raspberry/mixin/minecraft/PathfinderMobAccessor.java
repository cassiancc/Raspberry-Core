package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PathfinderMob.class)
public interface PathfinderMobAccessor {
    @Invoker("shouldStayCloseToLeashHolder")
    boolean callShouldStayCloseToLeashHolder();

    @Invoker("followLeashSpeed")
    double callFollowLeashSpeed();

    @Invoker("onLeashDistance")
    void callOnLeashDistance(float distance);
}