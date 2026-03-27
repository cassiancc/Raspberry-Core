package cc.cassian.raspberry.mixin.aquaculture;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teammetallurgy.aquaculture.api.AquacultureAPI;
import com.teammetallurgy.aquaculture.api.fishing.Hook;
import com.teammetallurgy.aquaculture.api.fishing.Hooks;
import com.teammetallurgy.aquaculture.entity.AquaFishingBobberEntity;
import com.teammetallurgy.aquaculture.init.AquaItems;
import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import com.teammetallurgy.aquaculture.misc.AquaConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.teammetallurgy.aquaculture.item.AquaFishingRodItem.*;

@Pseudo
@Mixin(AquaFishingRodItem.class)
public abstract class AquaFishingRodItemMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item uhhh(ItemStack instance) {
        return AquaItems.WORM.get();
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean properBait(Level instance, Entity aquaFishingBobberEntity, Operation<Boolean> original,
                               @Local(argsOnly = true) Player player, @Local(ordinal = 0) ItemStack heldStack) {
        aquaFishingBobberEntity.discard(); // Discard original AquaFishingBobberEntity

        ItemStack bait = getBait(heldStack);
        Hook hook = getHookType(heldStack);
        boolean isAdminRod = (Boolean) AquaConfig.BASIC_OPTIONS.debugMode.get() && ((AquaFishingRodItem) (Object) this).getTier() == AquacultureAPI.MATS.NEPTUNIUM;
        int lureSpeed = EnchantmentHelper.getFishingSpeedBonus(heldStack);
        if (((AquaFishingRodItem) (Object) this).getTier() == AquacultureAPI.MATS.NEPTUNIUM) {
            ++lureSpeed;
        }
        if (!isAdminRod && !bait.isEmpty()) {
            if (bait.is(RaspberryTags.BAD_BAIT)) {
                lureSpeed += ModConfig.get().aquaculture_badBaitLureBonus;
            } else if (bait.is(RaspberryTags.MID_BAIT)) {
                lureSpeed += ModConfig.get().aquaculture_midBaitLureBonus;
            } else if (bait.is(RaspberryTags.GOOD_BAIT)) {
                lureSpeed += ModConfig.get().aquaculture_goodBaitLureBonus;
            }
        }
        int luck = EnchantmentHelper.getFishingLuckBonus(heldStack);
        if (hook != Hooks.EMPTY && hook.getLuckModifier() > 0) {
            luck += hook.getLuckModifier();
        }
        lureSpeed = Math.min(5, lureSpeed);

        Entity bobber;
        if (hook.getName() != null && hook.getName().equals("grappling")) {
            boolean isSticky = !bait.isEmpty() && bait.is(RaspberryTags.STICKY_BAIT);
            bobber = new GrapplingHookEntity(player, player.level, luck, lureSpeed, getFishingLine(heldStack), getBobber(heldStack), heldStack, isSticky);
        } else {
            bobber = new AquaFishingBobberEntity(player, player.level, luck, lureSpeed, hook, getFishingLine(heldStack), getBobber(heldStack), heldStack);
        }
        return player.level.addFreshEntity(bobber);
    }

    @Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", target = "com/teammetallurgy/aquaculture/item/AquaFishingRodItem.getHookType (Lnet/minecraft/world/item/ItemStack;)Lcom/teammetallurgy/aquaculture/api/fishing/Hook;"), cancellable = true)
    private void retrieveGrapplingHook(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        GrapplingHookEntity grapplingHook = ((PlayerWithGrapplingHook)player).raspberryCore$getHook();

        if (grapplingHook != null) {
            ItemStack heldStack = player.getItemInHand(hand);
            Hook hook = getHookType(heldStack);
            AquaFishingRodItem rodItem = (AquaFishingRodItem) (Object) this;
            boolean isAdminRod = AquaConfig.BASIC_OPTIONS.debugMode.get() && rodItem.getTier() == AquacultureAPI.MATS.NEPTUNIUM;
            if (!level.isClientSide) {
                int rodDamage = grapplingHook.retrieve(heldStack);
                int currentDamage = rodItem.getMaxDamage(heldStack) - rodItem.getDamage(heldStack);
                if (rodDamage >= currentDamage) {
                    rodDamage = currentDamage;
                }
                if (!isAdminRod) {
                    if (hook != Hooks.EMPTY && hook.getDurabilityChance() > (double)0.0F) {
                        if (level.random.nextDouble() >= hook.getDurabilityChance()) {
                            heldStack.hurt(rodDamage, level.random, null);
                        }
                    } else {
                        heldStack.hurt(rodDamage, level.random, null);
                    }
                }
            }

            player.swing(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

            cir.setReturnValue(InteractionResultHolder.sidedSuccess(heldStack, level.isClientSide()));
        }

    }

}
