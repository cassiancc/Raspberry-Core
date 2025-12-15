package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.KnotConnectionAccess;
import cc.cassian.raspberry.compat.vanillabackport.leash.KnotInteractionHelper;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends Block { 

    public FenceBlockMixin(Properties properties) { super(properties); }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.get().backportLeash) return;

        List<LeashFenceKnotEntity> knots = level.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(pos));
        LeashFenceKnotEntity knot = knots.isEmpty() ? null : knots.get(0);

        if (knot != null) {
            InteractionResult result = KnotInteractionHelper.handleKnotInteraction(player, hand, knot);
            if (result != InteractionResult.PASS) {
                cir.setReturnValue(result);
            }
            return;
        }

        if (level.isClientSide) {
            KnotInteractionHelper.HeldEntities held = new KnotInteractionHelper.HeldEntities(player);
            if (!held.isEmpty() || KnotInteractionHelper.hasLeadItem(player)) {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            return;
        }

        KnotInteractionHelper.HeldEntities held = new KnotInteractionHelper.HeldEntities(player);
        
        if (KnotInteractionHelper.hasLeadItem(player) && held.isEmpty()) {
            knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            knot.playPlacementSound();
            KnotInteractionHelper.handleKnotInteraction(player, hand, knot);
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (!held.isEmpty()) {
            knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            knot.playPlacementSound();
            InteractionResult result = KnotInteractionHelper.handleKnotInteraction(player, hand, knot);
            if (result != InteractionResult.PASS) {
                cir.setReturnValue(result);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            
            List<LeashFenceKnotEntity> knots = level.getEntitiesOfClass(
                LeashFenceKnotEntity.class, 
                new AABB(pos), 
                knot -> knot.getPos().equals(pos)
            );
            
            for (LeashFenceKnotEntity knot : knots) {
                if (knot instanceof KnotConnectionAccess) {
                    KnotInteractionHelper.discardCustomConnections(knot, null);
                }
                knot.discard(); 
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}