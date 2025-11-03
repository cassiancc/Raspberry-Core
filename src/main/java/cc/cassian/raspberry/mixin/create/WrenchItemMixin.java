package cc.cassian.raspberry.mixin.create;

import cc.cassian.raspberry.client.ClientHelpers;
import cc.cassian.raspberry.config.ModConfig;
import com.simibubi.create.AllItems;
import net.mehvahdjukaar.supplementaries.common.items.WrenchItem;
import net.mehvahdjukaar.supplementaries.reg.ModParticles;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(WrenchItem.class)
public abstract class WrenchItemMixin {

    @Inject(method = "useOn", at = @At(value = "HEAD"), cancellable = true)
    public void use(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModConfig.get().unified_wrenches) {
            InteractionResult interactionResult = AllItems.WRENCH.get().useOn(context);
            var player = context.getPlayer();
            if (interactionResult == InteractionResult.SUCCESS) {
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, context.getClickedPos(), context.getItemInHand());
                    context.getLevel().gameEvent(player, GameEvent.BLOCK_CHANGE, context.getClickedPos());
                } else {
                    if (!ClientHelpers.isShiftDown())
                        raspberryCore$playTurningParticles(context.getClickedPos(), false, context.getHorizontalDirection(), context.getLevel(), player);
                }
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Unique
    private static void raspberryCore$playTurningParticles(BlockPos pos, boolean shiftDown, Direction dir, Level level, Player player) {
        if (net.mehvahdjukaar.supplementaries.configs.ClientConfigs.Items.WRENCH_PARTICLES.get()) {
            if (dir == Direction.DOWN) {
                shiftDown = !shiftDown;
            }

            level.addParticle(ModParticles.ROTATION_TRAIL_EMITTER.get(), (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, dir.get3DDataValue(), 0.71, shiftDown ? (double)1.0F : (double)-1.0F);
        }
    }
}
