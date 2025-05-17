package cc.cassian.raspberry.misc.toms_storage.filters;

import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagFilter implements Predicate<StoredItemStack> {
    private final List<ITag<Item>> itemTags;
    private final List<ITag<Block>> blockTags;

    private static final ITagManager<Item> ITEM_TAG_MANAGER =
            Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
    private static final ITagManager<Block> BLOCK_TAG_MANAGER =
            Objects.requireNonNull(ForgeRegistries.BLOCKS.tags());

    private static <T> Predicate<ITag<T>> createTagFilterFromPattern(Pattern pattern) {
        return (tag) -> {
            String tagName = tag.getKey().location().toString().toLowerCase();
            return pattern.matcher(tagName).find();
        };
    }

    public TagFilter(Pattern pattern) {
        this.itemTags = ITEM_TAG_MANAGER.stream()
                .filter(createTagFilterFromPattern(pattern))
                .collect(Collectors.toList());
        this.blockTags = BLOCK_TAG_MANAGER.stream()
                .filter(createTagFilterFromPattern(pattern))
                .collect(Collectors.toList());
    }

    @Override
    public boolean test(StoredItemStack storedItemStack) {
        Item item = storedItemStack.getStack().getItem();
        boolean isInAnItemTag = this.itemTags.stream()
                .anyMatch(tag -> tag.contains(item));
        if (isInAnItemTag) return true;

        if (!(item instanceof BlockItem blockItem)) return false;

        return this.blockTags.stream()
                .anyMatch(tag -> tag.contains(blockItem.getBlock()));
    }
}
