package cc.cassian.raspberry.events;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.Map;

public class WikiTooltipEvent {
	public static final Map<Integer, String> MAP_COLORS = Map.<Integer, String>ofEntries(Map.entry(0, "NONE"),
			Map.entry(1, "GRASS"),
			Map.entry(2, "SAND"),
			Map.entry(3, "WOOL"),
			Map.entry(4, "FIRE"),
			Map.entry(5, "ICE"),
			Map.entry(6, "METAL"),
			Map.entry(7, "PLANT"),
			Map.entry(8, "SNOW"),
			Map.entry(9, "CLAY"),
			Map.entry(10, "DIRT"),
			Map.entry(11, "STONE"),
			Map.entry(12, "WATER"),
			Map.entry(13, "WOOD"),
			Map.entry(14, "QUARTZ"),
			Map.entry(15, "COLOR_ORANGE"),
			Map.entry(16, "COLOR_MAGENTA"),
			Map.entry(17, "COLOR_LIGHT_BLUE"),
			Map.entry(18, "COLOR_YELLOW"),
			Map.entry(19, "COLOR_LIGHT_GREEN"),
			Map.entry(20, "COLOR_PINK"),
			Map.entry(21, "COLOR_GRAY"),
			Map.entry(22, "COLOR_LIGHT_GRAY"),
			Map.entry(23, "COLOR_CYAN"),
			Map.entry(24, "COLOR_PURPLE"),
			Map.entry(25, "COLOR_BLUE"),
			Map.entry(26, "COLOR_BROWN"),
			Map.entry(27, "COLOR_GREEN"),
			Map.entry(28, "COLOR_RED"),
			Map.entry(29, "COLOR_BLACK"),
			Map.entry(30, "GOLD"),
			Map.entry(31, "DIAMOND"),
			Map.entry(32, "LAPIS"),
			Map.entry(33, "EMERALD"),
			Map.entry(34, "PODZOL"),
			Map.entry(35, "NETHER"),
			Map.entry(36, "TERRACOTTA_WHITE"),
			Map.entry(37, "TERRACOTTA_ORANGE"),
			Map.entry(38, "TERRACOTTA_MAGENTA"),
			Map.entry(39, "TERRACOTTA_LIGHT_BLUE"),
			Map.entry(40, "TERRACOTTA_YELLOW"),
			Map.entry(41, "TERRACOTTA_LIGHT_GREEN"),
			Map.entry(42, "TERRACOTTA_PINK"),
			Map.entry(43, "TERRACOTTA_GRAY"),
			Map.entry(44, "TERRACOTTA_LIGHT_GRAY"),
			Map.entry(45, "TERRACOTTA_CYAN"),
			Map.entry(46, "TERRACOTTA_PURPLE"),
			Map.entry(47, "TERRACOTTA_BLUE"),
			Map.entry(48, "TERRACOTTA_BROWN"),
			Map.entry(49, "TERRACOTTA_GREEN"),
			Map.entry(50, "TERRACOTTA_RED"),
			Map.entry(51, "TERRACOTTA_BLACK"),
			Map.entry(52, "CRIMSON_NYLIUM"),
			Map.entry(53, "CRIMSON_STEM"),
			Map.entry(54, "CRIMSON_HYPHAE"),
			Map.entry(55, "WARPED_NYLIUM"),
			Map.entry(56, "WARPED_STEM"),
			Map.entry(57, "WARPED_HYPHAE"),
			Map.entry(58, "WARPED_WART_BLOCK"),
			Map.entry(59, "DEEPSLATE"),
			Map.entry(60, "RAW_IRON"),
			Map.entry(61, "GLOW_LICHEN"));

	public static void wikiTooltip(ItemTooltipEvent event) {
		if (ModConfig.get().infoTooltips && event.getItemStack().getItem() instanceof BlockItem blockItem) {
			var tooltip = event.getToolTip();
			var block = blockItem.getBlock();
			BlockState state = block.defaultBlockState();
			tooltip.add(Component.literal("Light level: " + state.getLightEmission()));
			tooltip.add(Component.literal("Map color: " + MAP_COLORS.get(block.defaultMaterialColor().id)));
			tooltip.add(Component.literal("Blast resistance: " + block.getExplosionResistance()));
			tooltip.add(Component.literal("Hardness: " + block.defaultDestroyTime()));
			tooltip.add(Component.literal("Note block instrument: " + NoteBlockInstrument.byState(state)));
		}
	}
}
