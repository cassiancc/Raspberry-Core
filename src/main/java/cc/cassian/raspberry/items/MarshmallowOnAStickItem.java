package cc.cassian.raspberry.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MarshmallowOnAStickItem extends Item {
	private static final FoodProperties RAW_PROPERTIES = new FoodProperties.Builder().nutrition(2).alwaysEat().build();
	private static final FoodProperties COOKED_PROPERTIES = new FoodProperties.Builder().nutrition(4).alwaysEat().build();
	private static final FoodProperties CHARRED_PROPERTIES = new FoodProperties.Builder().nutrition(1).alwaysEat().build();
	public static final int COOKING_TIME = 60;
	public static final String COOK_TIME_HANDHELD = "CookTimeHandheld";
	public static final String COOKING = "Cooking";

	public MarshmallowOnAStickItem(Properties properties) {
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.hasTag()) {
			if (stack.getTag().contains("charred")) {
				return Component.translatable("item.raspberry.charred_marshmallow_on_a_stick");
			}
			else if (stack.getTag().contains("cooked")) {
				return Component.translatable("item.raspberry.cooked_marshmallow_on_a_stick");
			}
		}
		return super.getName(stack);
	}

	private static boolean isPlayerNearHeatSource(Player player, LevelReader level) {
		if (player.isOnFire()) {
			return true;
		} else {
			BlockPos pos = player.blockPosition();

			for(BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
				if (level.getBlockState(nearbyPos).is(ModTags.HEAT_SOURCES)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		if (canCookOrIsCooking(stack)) {
			int fireAspectLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
			int cookingTime = stack.getOrCreateTag().getInt(COOK_TIME_HANDHELD);
			return SkilletBlock.getSkilletCookingTime(cookingTime, fireAspectLevel);
		} else {
			return super.getUseDuration(stack);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack cookingStack = player.getItemInHand(hand);
		boolean cooking = cookingStack.getOrCreateTag().contains(COOKING);
		if (isPlayerNearHeatSource(player, level)) {
			if (cooking) {
				player.startUsingItem(hand);
				return InteractionResultHolder.pass(cookingStack);
			}

			Optional<ItemStack> recipe = getCookingRecipe(cookingStack, level);
			if (recipe.isPresent()) {
				ItemStack cookingStackCopy = cookingStack.copy();
				ItemStack cookingStackUnit = cookingStackCopy.split(1);
				cookingStack.getOrCreateTag().put(COOKING, cookingStackUnit.serializeNBT());
				cookingStack.getOrCreateTag().putInt(COOK_TIME_HANDHELD, COOKING_TIME);
				player.startUsingItem(hand);
				return InteractionResultHolder.success(cookingStack);
			}
			return InteractionResultHolder.pass(cookingStack);
		} else {
			if (!cooking) {
				return super.use(level, player, hand);
			}
		}
		return InteractionResultHolder.pass(cookingStack);
	}

	@Override
	public boolean isEdible() {
		return true;
	}

	public UseAnim getUseAnimation(ItemStack itemStack) {
		if (canCookOrIsCooking(itemStack)) return UseAnim.NONE;
		if (itemStack.getItem().isEdible()) return UseAnim.EAT;
		return UseAnim.NONE;
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
		if (entity instanceof Player player) {
			Vec3 pos = player.position();
			double x = pos.x() + (double)0.5F;
			double y = pos.y();
			double z = pos.z() + (double)0.5F;
			if (level.random.nextInt(50) == 0) {
				level.playLocalSound(x, y, z, ModSounds.BLOCK_SKILLET_SIZZLE.get(), SoundSource.BLOCKS, 0.4F, level.random.nextFloat() * 0.2F + 0.9F, false);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (entity instanceof Player player) {
			CompoundTag tag = stack.getOrCreateTag();
			if (tag.contains(COOKING)) {
				tag.remove(COOKING);
				tag.remove(COOK_TIME_HANDHELD);
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof Player player) {
			CompoundTag tag = stack.getOrCreateTag();
			if (tag.contains(COOKING)) {
				Optional<ItemStack> cookingRecipe = getCookingRecipe(stack, level);
				cookingRecipe.ifPresent((resultStack) -> {

					stack.setCount(stack.getCount() - 1);

					resultStack.setCount(1);

					if (!player.getInventory().add(resultStack)) {
						player.drop(resultStack, false);
					}

					if (player instanceof ServerPlayer) {
						CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
					}

				});
				tag.remove(COOKING);
				tag.remove(COOK_TIME_HANDHELD);
			} else {
				return super.finishUsingItem(stack, level, entity);
			}
		}

		return stack;
	}

	public static Optional<ItemStack> getCookingRecipe(final ItemStack stack, Level level) {
		if (stack.isEmpty()) return Optional.empty();
		var newStack = stack.copy();
		newStack.setCount(1);
		CompoundTag compoundTag = new CompoundTag();
		if (newStack.hasTag()) {
			compoundTag = newStack.getTag();
		}
		if (!compoundTag.contains("cooked")) {
			compoundTag.putBoolean("cooked", true);
		} else if (!compoundTag.contains("charred")) {
			compoundTag.putBoolean("charred", true);
		}
		else return Optional.of(newStack);

		newStack.setTag(compoundTag);
		return Optional.of(newStack);
	}

	@Override
	public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
		if (stack.hasTag()) {
			if (stack.getTag().contains("charred")) {
				return CHARRED_PROPERTIES;
			}
			else if (stack.getTag().contains("cooked")) {
				return COOKED_PROPERTIES;
			}
		}
		return RAW_PROPERTIES;
	}

	public static boolean canCookOrIsCooking(LivingEntity livingEntity, ItemStack stack) {
		if (livingEntity instanceof Player player) {
			return isPlayerNearHeatSource(player, player.level);
		}
		return canCookOrIsCooking(stack);
	}

	public static boolean canCookOrIsCooking(ItemStack stack) {
		if (stack.hasTag()) {
			return stack.getTag().contains(COOKING);
		}

		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		var tag = Objects.requireNonNullElse(stack.getTag(), new CompoundTag());
		if (tag.contains(COOKING)) {
			tooltipComponents.add(Component.literal("Cooking"));
		}
	}
}
