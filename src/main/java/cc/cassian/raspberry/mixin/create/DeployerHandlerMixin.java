package cc.cassian.raspberry.mixin.create;

import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DeployerHandler.class, remap = false)
public class DeployerHandlerMixin {
    @Inject(method = "safeOnBeehiveUse(Lnet/minecraft/world/level/block/state/BlockState;" +
                        "Lnet/minecraft/world/level/Level;" +
                        "Lnet/minecraft/core/BlockPos;" +
                        "Lnet/minecraft/world/entity/player/Player;" +
                        "Lnet/minecraft/world/InteractionHand;" +
                        ")" + "Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "HEAD"),
            cancellable = true)
    private static void safeOnBeehiveUseTaggedShears(BlockState state, Level world, BlockPos pos,
                                                                  Player player, InteractionHand hand,
                                                                  CallbackInfoReturnable<InteractionResult> cir) {
        /* Mixing into if statements is kinda iffy. https://github.com/SpongePowered/Mixin/issues/365#issuecomment-539464542
         * Replacing the call to getItem() to return something else also didn't work for some reason.
         * That's why we're just reimplementing the shears logic :)
         * The code is more or less just copied wholesale from the default impl.
         */

        BeehiveBlock block = (BeehiveBlock) state.getBlock();
        ItemStack prevHeldItem = player.getItemInHand(hand);
        int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);
        if (honeyLevel < 5) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
            return;
        }

        // Delegate to the default impl if it's not tagged as #forge:shears
        if (!prevHeldItem.is(Tags.Items.SHEARS)) return;

        world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BEEHIVE_SHEAR,
                SoundSource.NEUTRAL, 1.0F, 1.0F);
        player.getInventory().placeItemBackInInventory(new ItemStack(Items.HONEYCOMB, 3));
        prevHeldItem.hurtAndBreak(1, player, s -> s.broadcastBreakEvent(hand));
        block.resetHoneyLevel(world, state, pos);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
