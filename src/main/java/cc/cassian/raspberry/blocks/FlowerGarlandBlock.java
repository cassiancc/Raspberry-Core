package cc.cassian.raspberry.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;

import java.awt.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowerGarlandBlock extends Block {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((entry) -> (entry.getKey() != Direction.DOWN && entry.getKey() != Direction.UP)).collect(Util.toMap());;
    private static final VoxelShape WEST_AABB = Block.box(0.0F, 9.0F, 0.0F, 1.0F, 15.0F, 16.0F);
    private static final VoxelShape EAST_AABB = Block.box(15.0F, 9.0F, 0.0F, 16.0F, 15.0F, 16.0F);
    private static final VoxelShape NORTH_AABB = Block.box(0.0F, 9.0F, 0.0F, 16.0F, 15.0F, 1.0F);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0F, 9.0F, 15.0F, 16.0F, 15.0F, 16.0F);
    private static final VoxelShape SUPPORT_SHAPE = Block.box(0.0F, 13.0F, 0.0F, 16.0F, 16.0F, 16.0F);
    private final Map<BlockState, VoxelShape> shapesCache;

    public FlowerGarlandBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), cc.cassian.raspberry.blocks.FlowerGarlandBlock::calculateShape)));
    }

    private static VoxelShape calculateShape(BlockState state) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (state.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (state.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (state.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }

        return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapesCache.get(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.hasFaces(this.getUpdatedState(state, level, pos));
    }

    private boolean hasFaces(BlockState state) {
        return this.countFaces(state) > 0;
    }

    private int countFaces(BlockState state) {
        int i = 0;

        for(BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
            if (state.getValue(booleanproperty)) {
                ++i;
            }
        }

        return i;
    }

    private boolean canSupportAtFace(BlockGetter level, BlockPos pos, Direction direction) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return false;
        } else {
            BlockPos blockpos = pos.relative(direction);
            return isAcceptableNeighbour(level, blockpos, direction);
        }
    }

    public static boolean isAcceptableNeighbour(BlockGetter blockReader, BlockPos neighborPos, Direction attachedFace) {
        BlockState state = blockReader.getBlockState(neighborPos);

        VoxelShape neighborSupportFaceShape = state.getBlockSupportShape(blockReader, neighborPos).getFaceShape(attachedFace.getOpposite());
        VoxelShape neighborCollisionFaceShape = state.getCollisionShape(blockReader, neighborPos).getFaceShape(attachedFace.getOpposite());

        // Check that neighbour shape covers support shape
        return !Shapes.joinIsNotEmpty(neighborCollisionFaceShape, SUPPORT_SHAPE, BooleanOp.ONLY_SECOND) || !Shapes.joinIsNotEmpty(neighborSupportFaceShape, SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
    }

    private BlockState getUpdatedState(BlockState state, BlockGetter level, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = null;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (state.getValue(booleanproperty)) {
                boolean flag = this.canSupportAtFace(level, pos, direction);
                if (!flag) {
                    if (blockstate == null) {
                        blockstate = level.getBlockState(blockpos);
                    }

                    flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
                }

                state = state.setValue(booleanproperty, flag);
            }
        }

        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        } else {
            BlockState blockstate = this.getUpdatedState(state, level, currentPos);
            return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
        }
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        if (!blockPlaceContext.isSecondaryUseActive() && blockPlaceContext.getItemInHand().is(this.asItem())) {
            return this.countFaces(blockState) < PROPERTY_BY_DIRECTION.size();
        } else {
            return super.canBeReplaced(blockState, blockPlaceContext);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        boolean flag = blockstate.is(this);
        BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

        for(Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() != Direction.Axis.Y) {
                BooleanProperty booleanproperty = getPropertyForFace(direction);
                boolean flag1 = flag && blockstate.getValue(booleanproperty);
                if (!flag1 && this.canSupportAtFace(context.getLevel(), context.getClickedPos(), direction)) {
                    return blockstate1.setValue(booleanproperty, true);
                }
            }
        }

        return flag ? blockstate1 : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return (((state.setValue(NORTH, state.getValue(SOUTH))).setValue(EAST, state.getValue(WEST))).setValue(SOUTH, state.getValue(NORTH))).setValue(WEST, state.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return (((state.setValue(NORTH, state.getValue(EAST))).setValue(EAST, state.getValue(SOUTH))).setValue(SOUTH, state.getValue(WEST))).setValue(WEST, state.getValue(NORTH));
            }
            case CLOCKWISE_90 -> {
                return (((state.setValue(NORTH, state.getValue(WEST))).setValue(EAST, state.getValue(NORTH))).setValue(SOUTH, state.getValue(EAST))).setValue(WEST, state.getValue(SOUTH));
            }
            default -> {
                return state;
            }
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return (state.setValue(NORTH, state.getValue(SOUTH))).setValue(SOUTH, state.getValue(NORTH));
            }
            case FRONT_BACK -> {
                return (state.setValue(EAST, state.getValue(WEST))).setValue(WEST, state.getValue(EAST));
            }
            default -> {
                return super.mirror(state, mirror);
            }
        }
    }

    public static BooleanProperty getPropertyForFace(Direction face) {
        return PROPERTY_BY_DIRECTION.get(face);
    }
}
