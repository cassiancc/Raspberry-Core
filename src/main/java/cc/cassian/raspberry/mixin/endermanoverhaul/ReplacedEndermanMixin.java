package cc.cassian.raspberry.mixin.endermanoverhaul;

import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import tech.alexnijjar.endermanoverhaul.common.constants.ConstantAnimations;
import tech.alexnijjar.endermanoverhaul.common.entities.ReplacedEnderman;

import javax.annotation.Nullable;

@Mixin(ReplacedEnderman.class)
public abstract class ReplacedEndermanMixin {

    @Shadow(remap = false)
    @Nullable
    public abstract EnderMan getEndermanFromState(AnimationEvent<ReplacedEnderman> state);

    /**
     * @author evanbones
     * @reason Fixed typo in orginal method that caused animations to not play correctly.
     */
    @Overwrite(remap = false)
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(
            (ReplacedEnderman) (Object) this, 
            "controller", 
            0, 
            state -> {
                EnderMan enderman = this.getEndermanFromState(state);
                if (enderman == null) return PlayState.STOP;

                if (!state.isMoving()) {
                    state.getController().setAnimation(enderman.isCreepy() ?
                        ConstantAnimations.RUN :
                        ConstantAnimations.WALK);
                } else {
                    state.getController().setAnimation(ConstantAnimations.IDLE);
                }
                return PlayState.CONTINUE;
            }
        ));

        data.addAnimationController(new AnimationController<>(
            (ReplacedEnderman) (Object) this, 
            "creepy_controller", 
            0, 
            state -> {
                EnderMan enderman = this.getEndermanFromState(state);
                if (enderman == null) return PlayState.STOP;
                if (!enderman.isCreepy()) return PlayState.STOP;
                state.getController().setAnimation(ConstantAnimations.ANGRY);
                return PlayState.CONTINUE;
            }
        ));

        data.addAnimationController(new AnimationController<>(
            (ReplacedEnderman) (Object) this, 
            "hold_controller", 
            0, 
            state -> {
                EnderMan enderman = this.getEndermanFromState(state);
                if (enderman == null) return PlayState.STOP;
                if (enderman.getCarriedBlock() == null) return PlayState.STOP;
                state.getController().setAnimation(ConstantAnimations.HOLDING);
                return PlayState.CONTINUE;
            }
        ));

        data.addAnimationController(new AnimationController<>(
            (ReplacedEnderman) (Object) this, 
            "attack_controller", 
            0, 
            state -> {
                EnderMan enderman = this.getEndermanFromState(state);
                if (enderman == null) return PlayState.STOP;
                if (enderman.getAttackAnim(state.getPartialTick()) == 0) return PlayState.STOP;
                state.getController().setAnimation(ConstantAnimations.ATTACK);
                return PlayState.CONTINUE;
            }
        ));
    }
}