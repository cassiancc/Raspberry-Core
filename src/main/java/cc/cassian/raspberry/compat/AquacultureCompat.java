package cc.cassian.raspberry.compat;

import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class AquacultureCompat {
    public static void checkAndAddTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof AquaFishingRodItem) {
            ItemStack bait = AquaFishingRodItem.getBait(event.getItemStack());
            if (!bait.getItem().equals(Items.AIR))
                event.getToolTip().add(1, Component.translatable(bait.getDescriptionId()));
        }
    }
}
