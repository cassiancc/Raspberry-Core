package cc.cassian.raspberry.blocks;

import cc.cassian.raspberry.compat.BuzzierBeesCompat;
import cc.cassian.raspberry.compat.CavernsAndChasmsCompat;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.function.Supplier;

public class RaspberryCakeBlock extends Block {
    public static final IntegerProperty BITES;
    public static final IntegerProperty CANDLE_TYPE;
    public static final BooleanProperty LIT;
    protected static final VoxelShape[] SHAPE_BY_BITE;
    public final Supplier<Item> cakeSlice;
    public final String cakeSliceItemID;
    private static final Iterable<Vec3> PARTICLE_OFFSETS;

    public RaspberryCakeBlock(Properties properties, String cakeSlice) {
        super(properties);
        this.cakeSliceItemID = cakeSlice;
        this.cakeSlice = null;
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0).setValue(CANDLE_TYPE, 0).setValue(LIT, false));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LIT) ? 3 : 0;
    }


    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return PARTICLE_OFFSETS;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            this.getParticleOffsets(state).forEach((arg4) -> addParticlesAndSound(state, level, arg4.add(pos.getX(), pos.getY(), pos.getZ()), random));
        }
    }

    private static void addParticlesAndSound(BlockState state, Level level, Vec3 offset, RandomSource random) {
        int candleType = state.getValue(CANDLE_TYPE);
        float f = random.nextFloat();
        if (f < 0.3F) {
            level.addParticle(ParticleTypes.SMOKE, offset.x, offset.y, offset.z, 0.0F, 0.0F, 0.0F);
            if (f < 0.17F) {
                level.playLocalSound(offset.x + (double)0.5F, offset.y + (double)0.5F, offset.z + (double)0.5F, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
        if (candleType == 18) {
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, offset.x, offset.y, offset.z, 0.0F, 0.0F, 0.0F);
        }
        else if (candleType == 19) {
            level.addParticle(CavernsAndChasmsCompat.getCupricCandleFlame(), offset.x, offset.y, offset.z, 0.0F, 0.0F, 0.0F);
        }
        else {
            level.addParticle(ParticleTypes.SMALL_FLAME, offset.x, offset.y, offset.z, 0.0F, 0.0F, 0.0F);
        }
    }

    public ItemStack getCakeSliceItem() {
        if (this.cakeSlice != null) {
            return new ItemStack(this.cakeSlice.get());
        }
        else {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.cakeSliceItemID)).getDefaultInstance();
        }
    }

    public int getMaxBites() {
        return 7;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_BITE[state.getValue(BITES)];
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (level.isClientSide) {
            if (heldStack.is(ModTags.KNIVES)) {
                return this.cutSlice(level, pos, state, player);
            }

            if (heldStack.is(ItemTags.CANDLES)) {
                return this.placeCandle(level, pos, state, player, heldStack);
            }

            if (heldStack.is(Items.FLINT_AND_STEEL) || heldStack.is(Items.FIRE_CHARGE)) {
                return this.lightCandle(level, pos, state, player, heldStack);
            }

            if (this.consumeBite(level, pos, state, player) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }

            if (heldStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return (heldStack.is(ModTags.KNIVES) ? this.cutSlice(level, pos, state, player) :
                (heldStack.is(ItemTags.CANDLES) ? this.placeCandle(level, pos, state, player, heldStack) :
                (heldStack.is(Items.FLINT_AND_STEEL) || heldStack.is(Items.FIRE_CHARGE) ? this.lightCandle(level, pos, state, player, heldStack) :
                        this.consumeBite(level, pos, state, player))));
    }

    protected InteractionResult consumeBite(Level level, BlockPos pos, BlockState state, Player playerIn) {
        int candleType = state.getValue(CANDLE_TYPE);
        if (candleType > 0) {
            popCandle(level, pos, state, candleType, true);
            return InteractionResult.SUCCESS;
        }
        if (!playerIn.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            ItemStack sliceStack = this.getCakeSliceItem();
            FoodProperties sliceFood = sliceStack.getItem().getFoodProperties();
            playerIn.getFoodData().eat(sliceStack.getItem(), sliceStack);
            if (this.getCakeSliceItem().getItem().isEdible() && sliceFood != null) {
                for(Pair<MobEffectInstance, Float> pair : sliceFood.getEffects()) {
                    if (!level.isClientSide && pair.getFirst() != null && level.random.nextFloat() < pair.getSecond()) {
                        playerIn.addEffect(new MobEffectInstance(pair.getFirst()));
                    }
                }
            }

            int bites = state.getValue(BITES);
            if (bites < this.getMaxBites() - 1) {
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            } else {
                level.removeBlock(pos, false);
            }

            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
            return InteractionResult.SUCCESS;
        }
    }

    protected InteractionResult cutSlice(Level level, BlockPos pos, BlockState state, Player player) {
        int bites = state.getValue(BITES);
        int candleType = state.getValue(CANDLE_TYPE);
        if (candleType > 0) {
            popCandle(level, pos, state, candleType, true);
            return InteractionResult.SUCCESS;
        }
        if (bites < this.getMaxBites() - 1) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
        }

        Direction direction = player.getDirection().getOpposite();
        ItemUtils.spawnItemEntity(level, this.getCakeSliceItem(), (double)pos.getX() + (double)0.5F, (double)pos.getY() + 0.3, (double)pos.getZ() + (double)0.5F, (double)direction.getStepX() * 0.15, 0.05, (double)direction.getStepZ() * 0.15);
        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
        return InteractionResult.SUCCESS;
    }

    public void useItem(Player player, ItemStack itemStack) {
        if (!player.isCreative()) {
            if (itemStack.isDamageableItem()) {
                itemStack.setDamageValue(itemStack.getDamageValue() + 1);

                if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                    itemStack.shrink(1);
                }
            } else {
                itemStack.shrink(1);
            }
        }

    }

    protected InteractionResult placeCandle(Level level, BlockPos pos, BlockState state, Player player, ItemStack heldStack) {
        int bites = state.getValue(BITES);
        int candleType = state.getValue(CANDLE_TYPE);
        if (bites == 0 && candleType == 0) {
            level.setBlock(pos, state.setValue(CANDLE_TYPE, getCandleNum(heldStack)), 3);
            useItem(player, heldStack);
        } else if (candleType > 0) {
            popCandle(level, pos, state, candleType, true);
        }
        else {
            return InteractionResult.PASS;
        }

        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
        return InteractionResult.SUCCESS;
    }

    public void popCandle(Level level, BlockPos pos, BlockState state, int candleType, Boolean shouldSet) {
        if (state.getValue(LIT) && shouldSet) {
            level.setBlock(pos, state.setValue(LIT, false), 3);
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            if (shouldSet){
            level.setBlock(pos, state.setValue(CANDLE_TYPE, 0), 3);
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
            }

            ItemUtils.spawnItemEntity(level, this.getCandleItem(candleType), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 0.0, 0.05, 0.0);
        }
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            int candleType = state.getValue(CANDLE_TYPE);
            if (candleType > 0) {
                popCandle(level, pos, state, candleType, false);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


    protected InteractionResult lightCandle(Level level, BlockPos pos, BlockState state, Player player, ItemStack heldStack) {
        int candleType = state.getValue(CANDLE_TYPE);
        Boolean lit = state.getValue(LIT);
        if (candleType > 0 && !lit) {
            level.setBlock(pos, state.setValue(LIT, true), 3);
            useItem(player, heldStack);

            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide && projectile.isOnFire()) {
            int candleType = state.getValue(CANDLE_TYPE);
            Boolean lit = state.getValue(LIT);
            if (candleType > 0 && !lit) {
                level.setBlock(hit.getBlockPos(), state.setValue(LIT, true), 3);

                level.playSound(null, hit.getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public ItemStack getCandleItem(int candleType) {
        if (candleType == 1) {
            return new ItemStack(Items.CANDLE);
        }
        else if (candleType == 2) {
            return new ItemStack(Items.WHITE_CANDLE);
        }
        else if (candleType == 3) {
            return new ItemStack(Items.LIGHT_GRAY_CANDLE);
        }
        else if (candleType == 4) {
            return new ItemStack(Items.GRAY_CANDLE);
        }
        else if (candleType == 5) {
            return new ItemStack(Items.BLACK_CANDLE);
        }
        else if (candleType == 6) {
            return new ItemStack(Items.BROWN_CANDLE);
        }
        else if (candleType == 7) {
            return new ItemStack(Items.RED_CANDLE);
        }
        else if (candleType == 8) {
            return new ItemStack(Items.ORANGE_CANDLE);
        }
        else if (candleType == 9) {
            return new ItemStack(Items.YELLOW_CANDLE);
        }
        else if (candleType == 10) {
            return new ItemStack(Items.LIME_CANDLE);
        }
        else if (candleType == 11) {
            return new ItemStack(Items.GREEN_CANDLE);
        }
        else if (candleType == 12) {
            return new ItemStack(Items.CYAN_CANDLE);
        }
        else if (candleType == 13) {
            return new ItemStack(Items.LIGHT_BLUE_CANDLE);
        }
        else if (candleType == 14) {
            return new ItemStack(Items.BLUE_CANDLE);
        }
        else if (candleType == 15) {
            return new ItemStack(Items.PURPLE_CANDLE);
        }
        else if (candleType == 16) {
            return new ItemStack(Items.MAGENTA_CANDLE);
        }
        else if (candleType == 17) {
            return new ItemStack(Items.PINK_CANDLE);
        }
        else if (candleType == 18) {
            return new ItemStack(BuzzierBeesCompat.getSoulCandle());
        }
        else if (candleType == 19) {
            return new ItemStack(CavernsAndChasmsCompat.getCupricCandle());
        }
        else {
            return new ItemStack(Items.CANDLE);
        }
    }

    public int getCandleNum(ItemStack heldStack) {
        if (heldStack.is(Items.CANDLE)) {
            return 1;
        }
        else if (heldStack.is(Items.WHITE_CANDLE)) {
            return 2;
        }
        else if (heldStack.is(Items.LIGHT_GRAY_CANDLE)) {
            return 3;
        }
        else if (heldStack.is(Items.GRAY_CANDLE)) {
            return 4;
        }
        else if (heldStack.is(Items.BLACK_CANDLE)) {
            return 5;
        }
        else if (heldStack.is(Items.BROWN_CANDLE)) {
            return 6;
        }
        else if (heldStack.is(Items.RED_CANDLE)) {
            return 7;
        }
        else if (heldStack.is(Items.ORANGE_CANDLE)) {
            return 8;
        }
        else if (heldStack.is(Items.YELLOW_CANDLE)) {
            return 9;
        }
        else if (heldStack.is(Items.LIME_CANDLE)) {
            return 10;
        }
        else if (heldStack.is(Items.GREEN_CANDLE)) {
            return 11;
        }
        else if (heldStack.is(Items.CYAN_CANDLE)) {
            return 12;
        }
        else if (heldStack.is(Items.LIGHT_BLUE_CANDLE)) {
            return 13;
        }
        else if (heldStack.is(Items.BLUE_CANDLE)) {
            return 14;
        }
        else if (heldStack.is(Items.PURPLE_CANDLE)) {
            return 15;
        }
        else if (heldStack.is(Items.MAGENTA_CANDLE)) {
            return 16;
        }
        else if (heldStack.is(Items.PINK_CANDLE)) {
            return 17;
        }
        else if (heldStack.is(BuzzierBeesCompat.getSoulCandle().asItem())) {
            return 18;
        }
        else if (heldStack.is(CavernsAndChasmsCompat.getCupricCandle().asItem())) {
            return 19;
        }
        else {
            return 0;
        }
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing == Direction.DOWN && !stateIn.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getMaterial().isSolid();
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES, CANDLE_TYPE, LIT);
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return this.getMaxBites() - blockState.getValue(BITES);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    static {
        BITES = IntegerProperty.create("bites", 0, 6);
        CANDLE_TYPE = IntegerProperty.create("candle_type", 0, 19);
        LIT = AbstractCandleBlock.LIT;
        SHAPE_BY_BITE = new VoxelShape[]{Block.box(1.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(3.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(5.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(7.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(9.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(11.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F), Block.box(13.0F, 0.0F, 1.0F, 15.0F, 8.0F, 15.0F)};
        PARTICLE_OFFSETS = ImmutableList.of(new Vec3(0.5F, 1.0F, 0.5F));
    }
}
