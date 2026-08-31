package cc.cassian.raspberry.items;

import cc.cassian.raspberry.RaspberryMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MoltenBucketItem extends BucketItem {
	public static final DefaultDispenseItemBehavior BUCKET_BEHAVIOUR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		public ItemStack execute(BlockSource source, ItemStack stack) {
			DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
			BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
			Level level = source.getLevel();
			if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, stack)) {
				dispensiblecontaineritem.checkExtraContent(null, level, stack, blockpos);
				return new ItemStack(Items.BUCKET);
			} else {
				return this.defaultDispenseItemBehavior.dispense(source, stack);
			}
		}
	};
	private final Supplier<Block> cauldron;

	public MoltenBucketItem(String name, RegistryObject<FlowingFluid> moltenLead, Properties tab) {
		super(moltenLead, tab);
		this.cauldron = ()->ForgeRegistries.BLOCKS.getValue(RaspberryMod.locate(name + "_cauldron"));
		DispenserBlock.registerBehavior(this, BUCKET_BEHAVIOUR);
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
