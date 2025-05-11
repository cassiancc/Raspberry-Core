package cc.cassian.raspberry.blocks;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiFunction;

public class FlowerBedBlock extends BushBlock implements BonemealableBlock {
    public static final int MIN_FLOWERS = 1;
    public static final int MAX_FLOWERS = 4;
    public static final DirectionProperty FACING;
    public static final IntegerProperty AMOUNT;
    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES;


    public FlowerBedBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, 1));
    }

    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return !blockPlaceContext.isSecondaryUseActive() && blockPlaceContext.getItemInHand().is(this.asItem()) && blockState.getValue(AMOUNT) < 4 || super.canBeReplaced(blockState, blockPlaceContext);
    }

    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_BY_PROPERTIES.apply(blockState.getValue(FACING), blockState.getValue(AMOUNT));
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        return blockState.is(this) ? blockState.setValue(AMOUNT, Math.min(4, blockState.getValue(AMOUNT) + 1)) : this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AMOUNT);
    }


    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        int i = blockState.getValue(AMOUNT);
        if (i < 4) {
            serverLevel.setBlock(blockPos, blockState.setValue(AMOUNT, i + 1), 2);
        } else {
            popResource(serverLevel, blockPos, new ItemStack(this));
        }

    }

    public static VoxelShape singleEncompassing(VoxelShape voxelShape) {
        return voxelShape.isEmpty() ? Shapes.empty() : Shapes.box(voxelShape.min(Direction.Axis.X), voxelShape.min(Direction.Axis.Y), voxelShape.min(Direction.Axis.Z), voxelShape.max(Direction.Axis.X), voxelShape.max(Direction.Axis.Y), voxelShape.max(Direction.Axis.Z));
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        AMOUNT = IntegerProperty.create("flower_amount", MIN_FLOWERS, MAX_FLOWERS);
        SHAPE_BY_PROPERTIES = Util.memoize((direction, integer) -> {
            VoxelShape[] voxelShapes = new VoxelShape[]{Block.box(8.0F, 0.0F, 8.0F, 16.0F, 3.0F, 16.0F), Block.box(8.0F, 0.0F, 0.0F, 16.0F, 3.0F, 8.0F), Block.box(0.0F, 0.0F, 0.0F, 8.0F, 3.0F, 8.0F), Block.box(0.0F, 0.0F, 8.0F, 8.0F, 3.0F, 16.0F)};
            VoxelShape voxelShape = Shapes.empty();

            for(int i = 0; i < integer; ++i) {
                int j = Math.floorMod(i - direction.get2DDataValue(), 4);
                voxelShape = Shapes.or(voxelShape, voxelShapes[j]);
            }

            return singleEncompassing(voxelShape);
        });
    }
}
