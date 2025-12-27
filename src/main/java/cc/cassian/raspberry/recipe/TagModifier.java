package cc.cassian.raspberry.recipe;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.ItemObliteratorCompat;
import cc.cassian.raspberry.config.RecipeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class TagModifier {

    public static void apply() {
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        if (tagManager == null) {
            RaspberryMod.LOGGER.error("Failed to load TagModifier: Item Tag Manager is null.");
            return;
        }

        int removalCount = 0;
        List<TagRule> rules = RecipeConfig.loadTagRules();

        for (TagRule rule : rules) {
            try {
                switch (rule.getAction()) {
                    case REMOVE_ALL_TAGS -> {
                        if (rule.getItems() == null) continue;
                        for (ResourceLocation itemId : rule.getItems()) {
                            Item item = ForgeRegistries.ITEMS.getValue(itemId);
                            if (item != null) {
                                removalCount += removeAllTagsFrom(tagManager, item);
                            }
                        }
                    }
                    case REMOVE_FROM_TAG -> {
                        if (rule.getTags() == null || rule.getItems() == null) continue;
                        for (ResourceLocation tagId : rule.getTags()) {
                            TagKey<Item> key = tagManager.createTagKey(tagId);
                            ITag<Item> tag = tagManager.getTag(key);
                            for (ResourceLocation itemId : rule.getItems()) {
                                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                                if (item != null && tag.contains(item)) {
                                    removeFromTag(tag, item);
                                    removalCount++;
                                }
                            }
                        }
                    }
                    case CLEAR_TAG -> {
                        if (rule.getTags() == null) continue;
                        for (ResourceLocation tagId : rule.getTags()) {
                            TagKey<Item> key = tagManager.createTagKey(tagId);
                            ITag<Item> tag = tagManager.getTag(key);
                            if (!tag.isEmpty()) {
                                removalCount += tag.size();
                                clearTag(tag);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                RaspberryMod.LOGGER.error("Error processing tag rule: {}", rule, e);
            }
        }

        if (ModCompat.ITEM_OBLITERATOR) {
            try {
                for (Item item : ForgeRegistries.ITEMS) {
                    if (item != null && ItemObliteratorCompat.shouldHide(item.getDefaultInstance())) {
                        removalCount += removeAllTagsFrom(tagManager, item);
                    }
                }
            } catch (Exception e) {
                RaspberryMod.LOGGER.error("Error processing Item Obliterator integration", e);
            }
        }

        if (removalCount > 0) {
            RaspberryMod.LOGGER.info("TagModifier removed {} item-tag associations.", removalCount);
        }
    }

    private static int removeAllTagsFrom(ITagManager<Item> manager, Item item) {
        if (manager == null || item == null) return 0;

        int count = 0;
        try {
            Collection<TagKey<Item>> owningTags = manager.getReverseTag(item)
                    .map(holder -> holder.getTagKeys().toList())
                    .orElse(List.of());

            for (TagKey<Item> key : owningTags) {
                ITag<Item> tag = manager.getTag(key);
                removeFromTag(tag, item);
                count++;
            }
        } catch (Exception e) {
            RaspberryMod.LOGGER.error("Error removing all tags from item: {}", item.getDescriptionId(), e);
        }
        return count;
    }

    private static void clearTag(ITag<Item> tag) {
        if (tag == null) return;
        try {
            if (tag instanceof Collection) {
                ((Collection<?>) tag).clear();
            } else {
                Field contentsField = tag.getClass().getDeclaredField("contents");
                contentsField.setAccessible(true);
                Collection<?> contents = (Collection<?>) contentsField.get(tag);
                if (contents != null) {
                    contents.clear();
                }
            }
        } catch (Exception e) {
            RaspberryMod.LOGGER.error("Failed to clear tag: {}", tag, e);
        }
    }

    private static void removeFromTag(ITag<Item> tag, Item item) {
        if (tag == null || item == null) return;
        try {
            if (tag instanceof Collection) {
                ((Collection<?>) tag).remove(item);
            } else {
                Field contentsField = tag.getClass().getDeclaredField("contents");
                contentsField.setAccessible(true);
                Collection<?> contents = (Collection<?>) contentsField.get(tag);
                if (contents != null) {
                    contents.remove(item);
                }
            }
        } catch (Exception e) {
            RaspberryMod.LOGGER.error("Failed to remove item {} from tag {}", item.getDescriptionId(), tag, e);
        }
    }
}