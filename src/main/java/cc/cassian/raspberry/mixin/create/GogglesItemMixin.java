package cc.cassian.raspberry.mixin.create;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.SurvivalityCompat;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.venturecraft.gliders.common.item.GliderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GogglesItem.class)
public class GogglesItemMixin {
    @Inject(
            method = {"use"},
            at = {@At(
                    value = "NEW", target = "(Lnet/minecraft/world/InteractionResult;Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;", ordinal = 1)}
    )
    private void swapArmorViaSurvivality(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (ModCompat.SURVIVALITY && SurvivalityCompat.swappingEnabled()) {
            SurvivalityCompat.swap(user);
            if (!world.isClientSide) {
                user.awardStat(Stats.ITEM_USED.get((GogglesItem)(Object)this));
            }
        }
    }
}
