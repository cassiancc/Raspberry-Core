package cc.cassian.raspberry.blocks;

import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrickCounterBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<StairsShape> SHAPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape TOP;
    protected static final VoxelShape BASE_NW;
    protected static final VoxelShape BASE_NE;
    protected static final VoxelShape BASE_SW;
    protected static final VoxelShape BASE_SE;
    protected static final VoxelShape[] SHAPES;
    private static final int[] SHAPE_BY_STATE;

    private static VoxelShape[] makeShapes(VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
        return IntStream.range(0, 16).mapToObj((i) -> makeStairShape(i, slabShape, nwCorner, neCorner, swCorner, seCorner)).toArray((i) -> new VoxelShape[i]);
    }

    private static VoxelShape makeStairShape(int bitfield, VoxelShape slabShape, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
        VoxelShape voxelshape = slabShape;
        if ((bitfield & 1) != 0) {
            voxelshape = Shapes.or(slabShape, nwCorner);
        }

        if ((bitfield & 2) != 0) {
            voxelshape = Shapes.or(voxelshape, neCorner);
        }

        if ((bitfield & 4) != 0) {
            voxelshape = Shapes.or(voxelshape, swCorner);
        }

        if ((bitfield & 8) != 0) {
            voxelshape = Shapes.or(voxelshape, seCorner);
        }

        return voxelshape;
    }

    public BrickCounterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (isFullBlock(state)) {
            return Block.box(0,0,0,16,16,16);
        }
        return SHAPES[SHAPE_BY_STATE[this.getShapeIndex(state)]];
    }

    private int getShapeIndex(BlockState state) {
        return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return blockstate.setValue(SHAPE, getStairsShape(blockstate, context.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return direction.getAxis().isHorizontal() ? state.setValue(SHAPE, getStairsShape(state, level, currentPos)) : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    private static StairsShape getStairsShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        if (direction.getAxis() == Axis.Y) {
            return StairsShape.STRAIGHT;
        }

        BlockState blockstate = level.getBlockState(pos.relative(direction));
        if (isCounter(blockstate)) {
            Direction direction1 = blockstate.getValue(FACING);
            if (direction1.getAxis() != Axis.Y && direction1.getAxis() != state.getValue(FACING).getAxis() && canTakeShape(state, level, pos, direction1.getOpposite())) {
                if (direction1 == direction.getCounterClockWise()) {
                    return StairsShape.OUTER_LEFT;
                }

                return StairsShape.OUTER_RIGHT;
            }
        }

        BlockState blockstate1 = level.getBlockState(pos.relative(direction.getOpposite()));
        if (isCounter(blockstate1)) {
            Direction direction2 = blockstate1.getValue(FACING);
            if (direction2.getAxis() != Axis.Y && direction2.getAxis() != state.getValue(FACING).getAxis() && canTakeShape(state, level, pos, direction2)) {
                if (direction2 == direction.getCounterClockWise()) {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean canTakeShape(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        BlockState blockstate = level.getBlockState(pos.relative(face));
        return !isCounter(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING);
    }

    public static boolean isCounter(BlockState state) {
        return state.getBlock() instanceof BrickCounterBlock;
    }

    public static boolean isFullBlock(BlockState state) {
        return state.getValue(FACING).getAxis() == Axis.Y;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction direction = state.getValue(FACING);
        StairsShape stairsshape = state.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Axis.Z) {
                    switch (stairsshape) {
                        case INNER_LEFT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        }
                        case INNER_RIGHT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        }
                        case OUTER_LEFT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        }
                        case OUTER_RIGHT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        }
                        default -> {
                            return state.rotate(Rotation.CLOCKWISE_180);
                        }
                    }
                }
                break;
            case FRONT_BACK:
                if (direction.getAxis() == Axis.X) {
                    switch (stairsshape) {
                        case INNER_LEFT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        }
                        case INNER_RIGHT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        }
                        case OUTER_LEFT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        }
                        case OUTER_RIGHT -> {
                            return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        }
                        case STRAIGHT -> {
                            return state.rotate(Rotation.CLOCKWISE_180);
                        }
                    }
                }
        }

        return super.mirror(state, mirror);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluid == Fluids.WATER && !isFullBlock(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    static {
        FACING = DirectionalBlock.FACING;
        SHAPE = BlockStateProperties.STAIRS_SHAPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        TOP = Block.box(0.0F, 12.0F, 0.0F, 16.0F, 16.0F, 16.0F);
        BASE_NW = Block.box(0.0F, 0.0F, 0.0F, 8.0F, 12.0F, 8.0F);
        BASE_NE = Block.box(8.0F, 0.0F, 0.0F, 16.0F, 12.0F, 8.0F);
        BASE_SW = Block.box(0.0F, 0.0F, 8.0F, 8.0F, 12.0F, 16.0F);
        BASE_SE = Block.box(8.0F, 0.0F, 8.0F, 16.0F, 12.0F, 16.0F);
        SHAPES = makeShapes(TOP, BASE_NW, BASE_NE, BASE_SW, BASE_SE);
        SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
    }
}
