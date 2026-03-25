package cc.cassian.raspberry.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Supplier;

public class MoltenCauldronBlock extends LavaCauldronBlock {
	private final Supplier<? extends Item> bucket;

	public MoltenCauldronBlock(Supplier<? extends Item> bucket, Properties arg) {
		super(arg);
		this.bucket = bucket;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		var stack = player.getItemInHand(hand);
		if (stack.is(Items.BUCKET)) {
			stack.setCount(stack.getCount()-1);
			player.getInventory().add(bucket.get().getDefaultInstance());
			level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return Items.CAULDRON.getDefaultInstance();
	}
}
