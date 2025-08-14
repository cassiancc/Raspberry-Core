package com.simibubi.create.compat.recipeViewerCommon;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class HiddenItems {
	/**
	 * A List of items to be hidden in REI/EMI due to them adding all items no matter if they are in the creative tab.
	 * <p>
	 * Copy of AllCreativeModeTabs from 1.20, should always be kept in sync
	 */
	public static Predicate<Item> getHiddenPredicate() {
		Set<Item> exclusions = new ReferenceOpenHashSet<>();

		List<ItemProviderEntry<?>> simpleExclusions = List.of(
				AllItems.INCOMPLETE_PRECISION_MECHANISM,
				AllItems.INCOMPLETE_REINFORCED_SHEET,
				AllItems.INCOMPLETE_TRACK,
				AllItems.CHROMATIC_COMPOUND,
				AllItems.SHADOW_STEEL,
				AllItems.REFINED_RADIANCE,
				AllItems.COPPER_BACKTANK_PLACEABLE,
				AllItems.NETHERITE_BACKTANK_PLACEABLE,
				AllItems.MINECART_CONTRAPTION,
				AllItems.FURNACE_MINECART_CONTRAPTION,
				AllItems.CHEST_MINECART_CONTRAPTION,
				AllItems.SCHEMATIC,
				AllBlocks.ANDESITE_ENCASED_SHAFT,
				AllBlocks.BRASS_ENCASED_SHAFT,
				AllBlocks.ANDESITE_ENCASED_COGWHEEL,
				AllBlocks.BRASS_ENCASED_COGWHEEL,
				AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL,
				AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL,
				AllBlocks.MYSTERIOUS_CUCKOO_CLOCK,
				AllBlocks.ELEVATOR_CONTACT,
				AllBlocks.SHADOW_STEEL_CASING,
				AllBlocks.REFINED_RADIANCE_CASING
		);

		for (ItemProviderEntry<?> entry : simpleExclusions) {
			exclusions.add(entry.get().asItem());
		}

		return exclusions::contains;
	}
}
