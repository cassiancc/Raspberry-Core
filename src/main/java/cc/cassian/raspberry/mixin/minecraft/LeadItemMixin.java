package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.KnotInteractionHelper;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LeadItem.class)
public class LeadItemMixin {
    
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void raspberry$onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.get().backportLeash) return;
        
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        Player player = context.getPlayer();
        
        if (!blockState.is(BlockTags.FENCES) || player == null) {
            return;
        }
        
        if (level.isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        
        List<LeashFenceKnotEntity> knots = level.getEntitiesOfClass(
            LeashFenceKnotEntity.class,
            new AABB(pos)
        );
        
        LeashFenceKnotEntity knot = knots.isEmpty() ? null : knots.get(0);
        KnotInteractionHelper.HeldEntities held = new KnotInteractionHelper.HeldEntities(player);
        
        if (knot != null) {
            InteractionResult result = KnotInteractionHelper.handleKnotInteraction(
                player, 
                context.getHand(), 
                knot
            );
            cir.setReturnValue(result);
            return;
        }
        
        if (!held.isEmpty()) {
            knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            knot.playPlacementSound();
            
            InteractionResult result = KnotInteractionHelper.handleKnotInteraction(
                player, 
                context.getHand(), 
                knot
            );
            cir.setReturnValue(result);
            return;
        }

        if (held.isEmpty() && knot == null) {
            knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            knot.playPlacementSound();
            
            InteractionResult result = KnotInteractionHelper.handleKnotInteraction(
                player,
                context.getHand(),
                knot
            );
            cir.setReturnValue(result);
            return;
        }
    }
}