package cc.cassian.raspberry.blocks;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RakedGravelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;

public class RaspberryGravelBlock extends GravelBlock {
    private final int colour;

    public RaspberryGravelBlock(Properties properties, int colour) {
        super(properties);
        this.colour = colour;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return colour;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Block block;
        if (player.getItemInHand(hand).getItem() instanceof HoeItem) {
            if (state.is(RaspberryBlocks.getBlock(RaspberryBlocks.DEEPSLATE_GRAVEL))) {
                level.setBlock(pos, RakedGravelBlock.getConnectedState(RaspberryBlocks.getBlock(RaspberryBlocks.RAKED_DEEPSLATE_GRAVEL).defaultBlockState(), level, pos, player.getDirection()), 0);
                if (!level.isClientSide) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    serverPlayer.getItemInHand(hand).hurtAndBreak(1, player, item ->
                            player.broadcastBreakEvent(hand));
                }
                player.playSound(SoundEvents.HOE_TILL);
                return InteractionResult.SUCCESS;
            }
            else if (state.is(RaspberryBlocks.getBlock(RaspberryBlocks.BLACKSTONE_GRAVEL))) {
                level.setBlock(pos, RakedGravelBlock.getConnectedState(RaspberryBlocks.getBlock(RaspberryBlocks.RAKED_BLACKSTONE_GRAVEL).defaultBlockState(), level, pos, player.getDirection()), 0);
                if (!level.isClientSide) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    serverPlayer.getItemInHand(hand).hurtAndBreak(1, player, item ->
                            player.broadcastBreakEvent(hand));
                }
                player.playSound(SoundEvents.HOE_TILL);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
