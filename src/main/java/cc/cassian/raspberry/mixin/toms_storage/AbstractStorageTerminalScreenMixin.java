package cc.cassian.raspberry.mixin.toms_storage;

import com.google.common.cache.LoadingCache;
import com.tom.storagemod.gui.AbstractStorageTerminalScreen;
import com.tom.storagemod.gui.PlatformEditBox;
import com.tom.storagemod.gui.StorageTerminalMenu;
import com.tom.storagemod.platform.Platform;
import com.tom.storagemod.util.IAutoFillTerminal;
import com.tom.storagemod.util.StoredItemStack;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mixin(value = AbstractStorageTerminalScreen.class, remap = false)
public abstract class AbstractStorageTerminalScreenMixin {
    @Shadow
    private PlatformEditBox searchField;

    @Shadow
    private boolean refreshItemList;

    @Shadow
    private String searchLast;

    @Shadow
    private static LoadingCache<StoredItemStack, List<String>> tooltipCache;

    @Shadow
    private StoredItemStack.IStoredItemStackComparator comparator;

    @Shadow
    private Comparator<StoredItemStack> sortComp;

    @Shadow
    protected float currentScroll;

    @Shadow
    protected int searchType;

    @Shadow
    protected abstract void onUpdateSearch(String text);

    @Nullable
    private Pattern queryToRegex(String regex) {
        try {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            try {
                return Pattern.compile(Pattern.quote(regex), Pattern.CASE_INSENSITIVE);
            } catch (PatternSyntaxException e2) {
                return null;
            }
        }
    }

    private boolean queriesMatch(List<Pattern> queries, String name) {
        if (queries.isEmpty()) return true;

        for (Pattern pattern : queries) {
            if (pattern.matcher(name).find()) return true;
        }

        return false;
    }

    private boolean queriesMatchSimple(List<Pattern> queries, StoredItemStack storedStack) {
        ItemStack stack = storedStack.getStack();

        String realName = stack.getItem().getName(stack).getString().toLowerCase();
        if (queriesMatch(queries, realName)) return true;

        String displayName = stack.getHoverName().getString().toLowerCase();
        return queriesMatch(queries, displayName);
    }

    private boolean queriesMatchTooltip(List<Pattern> queries, StoredItemStack storedStack) {
        List<String> tooltips;
        try {
            tooltips = tooltipCache.get(storedStack);
        } catch (Exception e) {
            return false;
        }

        for (String tooltip : tooltips) {
            if (queriesMatch(queries, tooltip)) return true;
        }

        return false;
    }

    private boolean queriesMatchTag(List<Pattern> queries, ITag<?> tag) {
        String tagString = tag.getKey().location().toString();
        return queriesMatch(queries, tagString);
    }

    private boolean tagsMatchItem(List<ITag<Item>> itemTags, List<ITag<Block>> blockTags, Item item) {
        boolean itemTagsMatch = itemTags.stream().anyMatch(tag -> tag.contains(item));
        boolean blockTagsMatch = false;
        if (item instanceof BlockItem blockItem) {
            blockTagsMatch = blockTags.stream().anyMatch(tag -> tag.contains(blockItem.getBlock()));
        }

        return itemTagsMatch || blockTagsMatch;
    }

    private void resetScroll(StorageTerminalMenu menu, String query) {
        menu.scrollTo(0.0F);
        this.currentScroll = 0.0F;
        if ((this.searchType & 4) > 0) {
            IAutoFillTerminal.sync(query);
        }

        if ((this.searchType & 2) > 0) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("s", query);
            menu.sendMessage(nbt);
        }

        this.onUpdateSearch(query);
    }

    @Inject(method = "updateSearch()V", at = @At("HEAD"), cancellable = true)
    private void updateSearch(CallbackInfo ci) {
        // This is a heavy-handed approach, but let's be real:
        // No one is going to mix into this method other than us. :)
        ci.cancel();

        String query = this.searchField.getValue();

        if (!this.refreshItemList && this.searchLast.equals(query)) return;

        List<Pattern> modQueries = new ArrayList<>();
        List<Pattern> simpleQueries = new ArrayList<>();
        List<Pattern> tooltipQueries = new ArrayList<>();
        List<Pattern> tagQueries = new ArrayList<>();

        for (String queryPart : query.split(" ")) {
            if (queryPart.isEmpty()) continue;

            Pattern prefixPattern = queryToRegex(queryPart.substring(1));
            Pattern normalPattern = queryToRegex(queryPart);

            if (queryPart.startsWith("@")) {
                modQueries.add(prefixPattern);
                continue;
            } else if (queryPart.startsWith("#")) {
                tagQueries.add(prefixPattern);
                continue;
            } else if (queryPart.startsWith("$")) {
                tooltipQueries.add(prefixPattern);
                continue;
            }

            if (EmiConfig.searchModNameByDefault) modQueries.add(normalPattern);
            if (EmiConfig.searchTagsByDefault) tagQueries.add(normalPattern);
            if (EmiConfig.searchTooltipByDefault) tagQueries.add(normalPattern);
            simpleQueries.add(queryToRegex(queryPart));
        }

        List<ITag<Item>> itemTags = new ArrayList<>();
        List<ITag<Block>> blockTags = new ArrayList<>();
        if (!tagQueries.isEmpty()) {
            // noinspection DataFlowIssue
            ForgeRegistries.ITEMS.tags()
                    .stream()
                    .filter(tag -> queriesMatchTag(tagQueries, tag))
                    .forEach(itemTags::add);

            // noinspection DataFlowIssue
            ForgeRegistries.BLOCKS.tags()
                    .stream()
                    .filter(tag -> queriesMatchTag(tagQueries, tag))
                    .forEach(blockTags::add);
        }

        @SuppressWarnings("DataFlowIssue")
        var screen = (AbstractStorageTerminalScreen<? extends StorageTerminalMenu>) (Object) this;
        StorageTerminalMenu menu = screen.getMenu();

        menu.itemListClientSorted.clear();

        for (StoredItemStack storedStack : menu.itemListClient) {
            if (storedStack == null || storedStack.getStack() == null) continue;

            ItemStack stack = storedStack.getStack();
            if (!queriesMatchSimple(simpleQueries, storedStack)) continue;

            Item item = stack.getItem();
            String modName = Platform.getItemId(item).getNamespace();
            if (!queriesMatch(modQueries, modName)) continue;

            if (!tagQueries.isEmpty() && !tagsMatchItem(itemTags, blockTags, item)) continue;
            if (!queriesMatchTooltip(tooltipQueries, storedStack)) continue;

            menu.itemListClientSorted.add(storedStack);
        }

        menu.itemListClientSorted.sort(menu.noSort ? this.sortComp : this.comparator);
        if (!this.searchLast.equals(query)) {
            this.resetScroll(menu, query);
        } else {
            menu.scrollTo(this.currentScroll);
        }

        this.refreshItemList = false;
        this.searchLast = query;
    }
}
