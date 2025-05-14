package cc.cassian.raspberry.client.tooltips;

import com.google.common.cache.CacheLoader;
import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TooltipCacheLoader extends CacheLoader<StoredItemStack, List<String>> {
    private static boolean isFakeShifting = false;
    public static boolean isFakeShifting() {
        return isFakeShifting;
    }

    /* ForgeHooksClient#gatherTooltipComponents is marked as @Internal.
     * I'm not a fan of using that, but
     *   a) EMI uses it too, and
     *   b) it's the only way I can see to make sure mods will definitely have added their tooltips.
     *
     * Why? The method sends out a RenderTooltipEvent.GatherComponents event onto the event bus,
     * which allows mods to modify or even cancel the tooltip components being rendered.
     *
     * Tooltips in Minecraft are really finicky, and this whole stringification thing is a sin,
     * so I think this is the lesser evil than accidentally missing some (weirdly lately) added
     * tooltips and confusing users.
     */
    @SuppressWarnings("UnstableApiUsage")
    private static List<ClientTooltipComponent> getClientTooltips(StoredItemStack storedStack) {
        Minecraft client = Minecraft.getInstance();
        ItemStack stack = storedStack.getStack();

        isFakeShifting = true;
        List<Component> defaultTooltips = stack.getTooltipLines(client.player, TooltipFlag.Default.NORMAL);

        List<ClientTooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(stack, defaultTooltips, 0,
                Integer.MAX_VALUE, Integer.MAX_VALUE, null, client.font);

        isFakeShifting = false;
        return components;
    }

    private static List<String> stringifyTooltips(List<ClientTooltipComponent> components) {
        List<String> tooltipLines = new ArrayList<>();
        for (ClientTooltipComponent component : components) {
            if (!(component instanceof ClientTextTooltip textTooltip)) continue;

            StringifyFormattedCharSink sink = new StringifyFormattedCharSink();
            textTooltip.text.accept(sink);
            tooltipLines.add(sink.toString());
        }

        return tooltipLines;
    }

    @Nonnull
    @Override
    public List<String> load(@Nonnull StoredItemStack storedStack) {
        List<ClientTooltipComponent> tooltips = getClientTooltips(storedStack);
        List<String> tooltipLines = stringifyTooltips(tooltips);

        // The first line would be the item name
        if (tooltipLines.isEmpty()) return tooltipLines;
        return tooltipLines.subList(1, tooltipLines.size());
    }
}
