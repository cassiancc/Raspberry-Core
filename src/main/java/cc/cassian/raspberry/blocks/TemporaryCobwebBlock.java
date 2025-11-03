package cc.cassian.raspberry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TemporaryCobwebBlock extends WebBlock {
    private static final int LIFETIME_TICKS = 100; // 5 seconds

    public TemporaryCobwebBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, LIFETIME_TICKS);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof ShearsItem) {
            return 0.15F;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }
}