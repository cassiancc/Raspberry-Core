package cc.cassian.raspberry.mixin.vc_gliders;

import cc.cassian.raspberry.compat.SurvivalityCompat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.venturecraft.gliders.common.item.GliderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GliderItem.class)
public class GliderItemMixin {
    @Inject(
            method = {"use"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/InteractionResultHolder;fail(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;"
            )}
    )
    private void swapArmorViaSurvivality(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (ModList.get().isLoaded("survivality") && SurvivalityCompat.swappingEnabled()) {
            SurvivalityCompat.swap(user);
            if (!world.isClientSide) {
                user.awardStat(Stats.ITEM_USED.get((GliderItem)(Object)this));
            }
        }

    }
}
