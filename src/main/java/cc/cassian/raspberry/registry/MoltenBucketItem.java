package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.RaspberryMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MoltenBucketItem extends BucketItem {
	private final Supplier<Block> cauldron;

	public MoltenBucketItem(String name, RegistryObject<FlowingFluid> moltenLead, Properties tab) {
		super(moltenLead, tab);
		this.cauldron = ()->ForgeRegistries.BLOCKS.getValue(RaspberryMod.locate(name + "_cauldron"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState blockState = level.getBlockState(blockPos);

		Player player = context.getPlayer();

		if (!blockState.is(Blocks.CAULDRON) || player == null)
			return InteractionResult.PASS;

		this.playEmptySound(player, level, blockPos);

		ItemStack bucket = context.getItemInHand();
		ItemStack emptyBucket = ItemUtils.createFilledResult(bucket, player, Items.BUCKET.getDefaultInstance());

		player.setItemInHand(context.getHand(), emptyBucket);
		level.setBlockAndUpdate(blockPos, cauldron.get().defaultBlockState());

		if (player instanceof ServerPlayer serverPlayer)
			CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, bucket);

		return InteractionResult.SUCCESS;
	}
}
