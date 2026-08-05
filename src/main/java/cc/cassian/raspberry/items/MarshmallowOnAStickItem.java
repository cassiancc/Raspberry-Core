package cc.cassian.raspberry.items;

import cc.cassian.raspberry.registry.RaspberryItems;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Optional;
import java.util.function.Consumer;

public class MarshmallowOnAStickItem extends Item {
	public static final FoodProperties RAW_PROPERTIES = new FoodProperties.Builder().nutrition(2).alwaysEat().build();
	public static final FoodProperties COOKED_PROPERTIES = new FoodProperties.Builder().nutrition(4).alwaysEat().build();
	public static final FoodProperties CHARRED_PROPERTIES = new FoodProperties.Builder().nutrition(1).alwaysEat().build();
	public static final int COOKING_TIME = 100;
	private static final String COOK_TIME_HANDHELD = "CookTimeHandheld";

	public MarshmallowOnAStickItem(Properties properties) {
		super(properties);
	}

	private static boolean isPlayerNearHeatSource(Player player, LevelReader level) {
		if (player.isOnFire()) {
			return true;
		} else {
			BlockPos pos = player.blockPosition();

			for(BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
				if (level.getBlockState(nearbyPos).is(ModTags.Blocks.HEAT_SOURCES)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		if (canCookOrIsCooking(stack)) {
			int fireAspectLevel = stack.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
			int cookingTime = stack.getOrCreateTag().getInt(COOK_TIME_HANDHELD);
			return SkilletBlock.getSkilletCookingTime(cookingTime, fireAspectLevel);
		} else {
			return super.getUseDuration(stack);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack cookingStack = player.getItemInHand(hand);
		boolean cooking = cookingStack.getOrCreateTag().contains(COOK_TIME_HANDHELD);
		if (isPlayerNearHeatSource(player, level)) {
			if (cooking) {
				player.startUsingItem(hand);
				return InteractionResultHolder.pass(cookingStack);
			}

			Optional<ItemStack> recipe = getCookingRecipe(cookingStack);
			if (recipe.isPresent()) {
				cookingStack.getOrCreateTag().putInt(COOK_TIME_HANDHELD, COOKING_TIME);
				player.startUsingItem(hand);
				return InteractionResultHolder.success(cookingStack);
			}
			return InteractionResultHolder.pass(cookingStack);
		} else {
			cookingStack.removeTagKey(COOK_TIME_HANDHELD);
			return super.use(level, player, hand);
		}
	}

	public UseAnim getUseAnimation(ItemStack itemStack) {
		if (canCookOrIsCooking(itemStack)) return UseAnim.NONE;
		if (itemStack.getItem().isEdible()) return UseAnim.EAT;
		return UseAnim.NONE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			@Override
			public boolean applyForgeHandTransform(PoseStack matrixStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTicks, float equipProcess, float swingProcess) {
				if (itemInHand.hasTag() && itemInHand.getTag().contains(COOK_TIME_HANDHELD)) {
					int i = arm == HumanoidArm.RIGHT ? 1 : -1;
					matrixStack.translate((float)i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
					matrixStack.translate(i*-.5, 0.2, -0.05);
					matrixStack.mulPose(Axis.YP.rotationDegrees(2));
					matrixStack.mulPose(Axis.XP.rotationDegrees(185));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(165));
					matrixStack.translate(-.25, 0 ,0);
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
		if (entity instanceof Player player && level.random.nextInt(50) == 0) {
			playLocalSound(level, player, RaspberrySoundEvents.MARSHMALLOW_SIZZLE.get());
		}
	}

	private static void playLocalSound(Level level, Player player, SoundEvent sound) {
		Vec3 pos = player.position();
		double x = pos.x() + (double) 0.5F;
		double y = pos.y();
		double z = pos.z() + (double) 0.5F;
		level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, 0.4F, level.random.nextFloat() * 0.2F + 0.9F, false);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (entity instanceof Player) {
			CompoundTag tag = stack.getOrCreateTag();
			if (tag.contains(COOK_TIME_HANDHELD)) {
				tag.remove(COOK_TIME_HANDHELD);
			}
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (entity instanceof Player player) {
			CompoundTag tag = stack.getOrCreateTag();
			if (tag.contains(COOK_TIME_HANDHELD)) {
				Optional<ItemStack> cookingRecipe = getCookingRecipe(stack);
				cookingRecipe.ifPresent((resultStack) -> {
					resultStack.setCount(1);

					if (resultStack.is(RaspberryItems.CHARRED_MARSHMALLOW_ON_A_STICK.get())) {
						playLocalSound(level, player, RaspberrySoundEvents.MARSHMALLOW_CHAR.get());
					}
					else if (resultStack.is(RaspberryItems.CARAMELIZED_MARSHMALLOW_ON_A_STICK.get())) {
						playLocalSound(level, player, RaspberrySoundEvents.MARSHMALLOW_CARAMELIZE.get());
					}

					if (stack.getCount()==1 && entity.getItemInHand(InteractionHand.MAIN_HAND).equals(stack)) {
						entity.setItemInHand(InteractionHand.MAIN_HAND, resultStack);
					} else if (stack.getCount()==1 && entity.getItemInHand(InteractionHand.OFF_HAND).equals(stack)) {
						entity.setItemInHand(InteractionHand.OFF_HAND, resultStack);
					} else {
						stack.setCount(stack.getCount() - 1);

						if (!player.getInventory().add(resultStack)) {
							player.drop(resultStack, false);
						}
					}

					if (player instanceof ServerPlayer) {
						CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
					}

				});
				tag.remove(COOK_TIME_HANDHELD);
			} else {
				return super.finishUsingItem(stack, level, entity);
			}
		}

		return stack;
	}

	public static Optional<ItemStack> getCookingRecipe(final ItemStack stack) {
		if (stack.isEmpty()) return Optional.empty();

		CompoundTag compoundTag = new CompoundTag();
		if (stack.hasTag()) {
			compoundTag = stack.getTag();
		}

		Item newItem;
		if (stack.is(RaspberryItems.MARSHMALLOW_ON_A_STICK.get())) {
			newItem = RaspberryItems.CARAMELIZED_MARSHMALLOW_ON_A_STICK.get();
		}
		else if (stack.is(RaspberryItems.CARAMELIZED_MARSHMALLOW_ON_A_STICK.get())) {
			newItem = RaspberryItems.CHARRED_MARSHMALLOW_ON_A_STICK.get();
		}
		else return Optional.empty();
		return Optional.of(new ItemStack(newItem, 1, compoundTag));
	}

	public static boolean canCookOrIsCooking(LivingEntity livingEntity, ItemStack stack) {
		if (livingEntity instanceof Player player) {
			return isPlayerNearHeatSource(player, player.level());
		}
		return canCookOrIsCooking(stack);
	}

	public static boolean canCookOrIsCooking(ItemStack stack) {
		if (stack.is(RaspberryItems.CHARRED_MARSHMALLOW_ON_A_STICK.get())) {
			return false;
		}
		if (stack.hasTag()) {
			return stack.getTag().contains(COOK_TIME_HANDHELD);
		}
		return false;
	}
}
