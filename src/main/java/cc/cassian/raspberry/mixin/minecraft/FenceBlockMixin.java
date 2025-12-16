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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends Block { 

    public FenceBlockMixin(Properties properties) { 
        super(properties); 
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.get().backportLeash) return;

        KnotInteractionHelper.HeldEntities held = new KnotInteractionHelper.HeldEntities(player);
        
        if (held.isEmpty()) {
            return; 
        }
        
        if (level.isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        
        List<LeashFenceKnotEntity> knots = level.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB(pos));
        LeashFenceKnotEntity knot = knots.isEmpty() ? null : knots.get(0);
        
        if (knot == null) {
            knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            knot.playPlacementSound();
        }
        
        InteractionResult result = KnotInteractionHelper.handleKnotInteraction(player, hand, knot);
        cir.setReturnValue(result);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
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