package cc.cassian.raspberry.mixin.aquaculture;

import cc.cassian.raspberry.config.ModConfig;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.teammetallurgy.aquaculture.item.AquaFishingRodItem.*;

@Pseudo
@Mixin(AquaFishingRodItem.class)
public abstract class AquaFishingRodItemMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
    private Item uhhh(ItemStack instance) {
        return AquaItems.WORM.get();
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean properBait(Level instance, Entity entity, Operation<Boolean> original,
                               @Local(argsOnly = true) Player player, @Local(ordinal = 0) ItemStack heldStack) {
        ItemStack bait = getBait(heldStack);
        Hook hook = getHookType(heldStack);
        boolean isAdminRod = (Boolean) AquaConfig.BASIC_OPTIONS.debugMode.get() && ((AquaFishingRodItem) (Object) this).getTier() == AquacultureAPI.MATS.NEPTUNIUM;
        int lureSpeed = EnchantmentHelper.getFishingSpeedBonus(heldStack);
        if (((AquaFishingRodItem) (Object) this).getTier() == AquacultureAPI.MATS.NEPTUNIUM) {
            ++lureSpeed;
        }
        if (!isAdminRod && !bait.isEmpty()) {
            if (bait.is(RaspberryTags.BAD_BAIT)) {
                lureSpeed += ModConfig.get().badBaitLureBonus;
            } else if (bait.is(RaspberryTags.MID_BAIT)) {
                lureSpeed += ModConfig.get().midBaitLureBonus;
            } else if (bait.is(RaspberryTags.GOOD_BAIT)) {
                lureSpeed += ModConfig.get().goodBaitLureBonus;
            }
        }
        int luck = EnchantmentHelper.getFishingLuckBonus(heldStack);
        if (hook != Hooks.EMPTY && hook.getLuckModifier() > 0) {
            luck += hook.getLuckModifier();
        }
        lureSpeed = Math.min(5, lureSpeed);
        Entity bobber = new AquaFishingBobberEntity(player, player.level, luck, lureSpeed, hook, getFishingLine(heldStack), getBobber(heldStack), heldStack);
        return player.level.addFreshEntity(bobber);
    }
}
