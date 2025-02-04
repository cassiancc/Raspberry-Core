package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class AquacultureCompat {
    public static void checkAndAddTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof AquaFishingRodItem) {
            ItemStack bait = AquaFishingRodItem.getBait(event.getItemStack());
            if (!bait.getItem().equals(Items.AIR))
                event.getToolTip().add(1, Component.translatable(bait.getDescriptionId()).append(checkAndAddCount(bait.getCount())).withStyle(checkAndAddStyle(bait)));
        }
    }

    public static MutableComponent checkAndAddCount(int count) {
        if (count != 1)
            return Component.literal(" x"+count);
        else return Component.empty();
    }

    public static ChatFormatting checkAndAddStyle(ItemStack bait) {
        if (bait.is(RaspberryTags.BAD_BAIT)) {
            return ChatFormatting.RED;
        } else if (bait.is(RaspberryTags.MID_BAIT)) {
            return ChatFormatting.GREEN;
        } else if (bait.is(RaspberryTags.GOOD_BAIT)) {
            return ChatFormatting.GOLD;
        } else return ChatFormatting.WHITE;
    }
}
