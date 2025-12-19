package cc.cassian.raspberry.mixin.connectiblechains;

import com.lilypuree.connectiblechains.entity.ChainKnotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChainKnotEntity.class)
public abstract class ChainKnotEntityBehaviorMixin extends HangingEntity {

    protected ChainKnotEntityBehaviorMixin(EntityType<? extends HangingEntity> p_31703_, Level p_31704_) {
        super(p_31703_, p_31704_);
    }

    @Shadow(remap = false) public abstract boolean canStayAttached();
    @Shadow(remap = false) public abstract void destroyLinks(boolean mayDrop);
    @Shadow(remap = false) public abstract void playPlacementSound();

    @Inject(method = "canStayAttached", at = @At("RETURN"), cancellable = true, remap = false)
    private void raspberry$checkForLeashKnot(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && !this.level().isClientSide()) {
            BlockPos pos = this.blockPosition();
            AABB aabb = new AABB(pos).inflate(1.0); 
            List<LeashFenceKnotEntity> knots = this.level().getEntitiesOfClass(LeashFenceKnotEntity.class, aabb);
            
            for (LeashFenceKnotEntity knot : knots) {
                if (knot.blockPosition().equals(pos) && knot.isAlive()) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void raspberry$tickCheckObstruction(CallbackInfo ci) {
        if (!this.level().isClientSide() && !this.canStayAttached()) {
            this.destroyLinks(true);
            this.discard();
        }
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lcom/lilypuree/connectiblechains/entity/ChainKnotEntity;destroyLinks(Z)V", remap = false))
    private void raspberry$damageShearsOnInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Tags.Items.SHEARS)) {
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }
    }

    /**
     * @author evanbones
     * @reason Fixes inability to punch-break knots and adds durability usage to shears on punch.
     */
    @Overwrite
    public boolean skipAttackInteraction(Entity attacker) {
        if (attacker instanceof Player player) {
            if (!this.level().isClientSide()) {
                this.playPlacementSound();
                this.destroyLinks(!player.isCreative());
                this.discard();

                ItemStack stack = player.getMainHandItem();
                if (stack.is(Tags.Items.SHEARS)) {
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
            }
            return true;
        }
        return false;
    }
}