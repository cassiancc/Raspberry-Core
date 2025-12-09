package cc.cassian.raspberry.blocks;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.baisylia.cookscollection.item.ModItems;
import com.teamabnormals.environmental.core.registry.EnvironmentalMobEffects;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PancakeBlock;
import net.mehvahdjukaar.supplementaries.common.items.PancakeItem;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class LemonPancakeBlock extends PancakeBlock {
    public LemonPancakeBlock(Properties copy) {
        super(copy);
        this.registerDefaultState(this.stateDefinition.any().setValue(PANCAKES, 1).setValue(LEMON_TOPPING, false).setValue(TOPPING, ModBlockProperties.Topping.NONE).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PANCAKES, TOPPING, LEMON_TOPPING, WATERLOGGED);
    }

    public static final BooleanProperty LEMON_TOPPING = BooleanProperty.create("lemon_topping");


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        ModBlockProperties.Topping t = ModBlockProperties.Topping.fromItem(stack);
        if (t != ModBlockProperties.Topping.NONE) {
            return InteractionResult.PASS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    public static InteractionResult place(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(handIn);
        if (!state.getOptionalValue(LEMON_TOPPING).orElse(false) && state.getValue(TOPPING) == ModBlockProperties.Topping.NONE) {
            if (stack.is(ModItems.LEMON.get())) {
                if (!worldIn.isClientSide) {
                    worldIn.setBlock(pos, RaspberryBlocks.LEMON_PANCAKE.get().withPropertiesOf(state).setValue(LEMON_TOPPING, true), 3);
                    worldIn.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.2F);
                }
                if (!player.isCreative()) {
                    Utils.swapItem(player, handIn, ItemStack.EMPTY);
                }

                return InteractionResult.sidedSuccess(worldIn.isClientSide);
            }
        } else {
            if (state.is(RaspberryBlocks.LEMON_PANCAKE.get())) {
                if (stack.getItem() instanceof PancakeItem && state.getOptionalValue(PANCAKES).orElse(0)<8) {
                    worldIn.setBlock(pos, state.setValue(PANCAKES, state.getValue(PANCAKES)+1), 3);
                    stack.setCount(stack.getCount()-1);
                    SoundType soundtype = state.getSoundType(worldIn, pos, player);
                    worldIn.playSound(
                            player,
                            pos,
                            SoundEvents.WOOL_PLACE,
                            SoundSource.BLOCKS,
                            (soundtype.getVolume() + 1.0F) / 2.0F,
                            soundtype.getPitch() * 0.8F
                    );
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    public static void removeLayer(BlockState state, BlockPos pos, Level world, Player player) {
        int i = state.getValue(PANCAKES);
        if (i == 8) {
            BlockPos up = pos.above();
            BlockState upState = world.getBlockState(up);
            if (upState.getBlock() == state.getBlock()) {
                removeLayer(upState, up, world, player);
                return;
            }
        }

        if (i > 1) {
            world.setBlock(pos, state.setValue(PANCAKES, i - 1), 3);
        } else {
            world.removeBlock(pos, false);
        }

        if (state.getValue(LEMON_TOPPING)) {
            player.addEffect(new MobEffectInstance(EnvironmentalMobEffects.PANIC.get(), 160));
        }

    }
}
