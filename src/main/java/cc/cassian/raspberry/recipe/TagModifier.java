package cc.cassian.raspberry.recipe;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.ItemObliteratorCompat;
import cc.cassian.raspberry.config.RecipeConfig;
import cc.cassian.raspberry.mixin.accessor.HolderSetNamedAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class TagModifier {

    public static void apply() {
        applyToRegistry(
                BuiltInRegistries.ITEM,
                ForgeRegistries.ITEMS,
                "Item"
        );

        applyToRegistry(
                BuiltInRegistries.BLOCK,
                ForgeRegistries.BLOCKS,
                "Block"
        );

        if (ModCompat.ITEM_OBLITERATOR) {
            applyItemObliterator();
        }
    }

    /**
     * Generic method to apply tag rules to any registry (Items, Blocks, etc.)
     */
    private static <T> void applyToRegistry(Registry<T> vanillaRegistry, IForgeRegistry<T> forgeRegistry, String debugName) {
        var tagManager = forgeRegistry.tags();
        if (tagManager == null) {
            RaspberryMod.LOGGER.error("Failed to load TagModifier: {} Tag Manager is null.", debugName);
            return;
        }

        int removalCount = 0;
        List<TagRule> rules = RecipeConfig.loadTagRules();

        for (TagRule rule : rules) {
            try {
                switch (rule.getAction()) {
                    case REMOVE_ALL_TAGS -> {
                        if (rule.getItems() == null) continue;
                        for (ResourceLocation id : rule.getItems()) {
                            T object = forgeRegistry.getValue(id);
                            if (object != null) {
                                removalCount += removeAllTagsFrom(vanillaRegistry, object);
                            }
                        }
                    }
                    case REMOVE_FROM_TAG -> {
                        if (rule.getTags() == null || rule.getItems() == null) continue;
                        for (ResourceLocation tagId : rule.getTags()) {
                            TagKey<T> key = tagManager.createTagKey(tagId);
                            var vanillaTag = vanillaRegistry.getTag(key).orElse(null);

                            for (ResourceLocation id : rule.getItems()) {
                                T object = forgeRegistry.getValue(id);
                                if (object != null && vanillaTag != null && vanillaTag.contains(vanillaRegistry.wrapAsHolder(object))) {
                                    removeFromTag(vanillaTag, object);
                                    removalCount++;
                                }
                            }
                        }
                    }
                    case CLEAR_TAG -> {
                        if (rule.getTags() == null) continue;
                        for (ResourceLocation tagId : rule.getTags()) {
                            TagKey<T> key = tagManager.createTagKey(tagId);
                            var vanillaTag = vanillaRegistry.getTag(key).orElse(null);

                            if (vanillaTag != null && vanillaTag.size() > 0) {
                                RaspberryMod.LOGGER.info("TagModifier: Clearing tag '{}' (contained {} items)", tagId, vanillaTag.size());
                                removalCount += vanillaTag.size();
                                clearTag(vanillaTag);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                RaspberryMod.LOGGER.error("Error processing {} tag rule: {}", debugName, rule, e);
            }
        }

        if (removalCount > 0) {
            RaspberryMod.LOGGER.info("TagModifier removed {} {}-tag associations.", removalCount, debugName);
        }
    }

    private static void applyItemObliterator() {
        int removalCount = 0;
        try {
            for (Item item : ForgeRegistries.ITEMS) {
                if (item != null && ItemObliteratorCompat.shouldHide(item.getDefaultInstance())) {
                    removalCount += removeAllTagsFrom(BuiltInRegistries.ITEM, item);

                    if (item instanceof BlockItem blockItem) {
                        Block block = blockItem.getBlock();
                        removalCount += removeAllTagsFrom(BuiltInRegistries.BLOCK, block);
                    }
                }
            }
        } catch (Exception e) {
            RaspberryMod.LOGGER.error("Error processing Item Obliterator integration", e);
        }

        if (removalCount > 0) {
            RaspberryMod.LOGGER.info("Item Obliterator integration removed {} item-tag associations.", removalCount);
        }
    }

    private static void clearTag(Object tag) {
        if (tag instanceof HolderSetNamedAccessor accessor) {
            accessor.setContents(new ArrayList<>());
        } else {
            RaspberryMod.LOGGER.debug("TagModifier: Tag object {} is not HolderSet.Named", tag.getClass().getSimpleName());
        }
    }

    private static <T> void removeFromTag(Object tag, T value) {
        if (tag instanceof HolderSetNamedAccessor accessor) {
            List<Holder<?>> currentContents = accessor.getContents();
            if (currentContents != null) {
                List<Holder<?>> mutableContents = new ArrayList<>(currentContents);
                if (mutableContents.removeIf(holder -> holder.value() == value)) {
                    accessor.setContents(mutableContents);
                }
            }
        }
    }

    private static <T> int removeAllTagsFrom(Registry<T> registry, T value) {
        if (value == null) return 0;
        int count = 0;

        var holder = registry.wrapAsHolder(value);
        var tags = holder.tags().toList();

        for (TagKey<T> key : tags) {
            var vanillaTag = registry.getTag(key).orElse(null);
            if (vanillaTag != null) {
                removeFromTag(vanillaTag, value);
                count++;
            }
        }
        return count;
    }
}