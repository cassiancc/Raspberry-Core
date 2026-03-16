package cc.cassian.raspberry.events;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChangeBlockEvent {
	public static boolean changeBlock(final Level level, final BlockPos pos, final Player player, final InteractionHand hand, Block oldBlock, Block newBlock) {
		if (!ModConfig.get().gloomyRuning) return false;
		ItemStack itemInHand = player.getItemInHand(hand);
		BlockState state = level.getBlockState(pos);
		if (state.is(oldBlock)) {
			level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
			itemInHand.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), RaspberrySoundEvents.BLOCK_CYCLE.get(), SoundSource.PLAYERS,
					(float) ModConfig.get().mirrorVolumeModifier, 1.0F + (float) (player.getRandom().nextGaussian() * 0.35));
//			player.getCooldowns().addCooldown(itemInHand.getItem(), 120);
			return true;
		}
		return false;
	}

	public static boolean changeBlock(Level level, Player player, InteractionHand hand, Block oldBlock, Block newBlock) {
		if (player.pick(player.getReachDistance(), 0, false) instanceof BlockHitResult blockHitResult) {
			return changeBlock(level, blockHitResult.getBlockPos(), player, hand, oldBlock, newBlock);
		}
		return false;
	}
}
