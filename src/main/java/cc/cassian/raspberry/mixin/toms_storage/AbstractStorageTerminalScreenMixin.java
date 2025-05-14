package cc.cassian.raspberry.mixin.toms_storage;

import cc.cassian.raspberry.misc.toms_storage.filters.AnyFilter;
import cc.cassian.raspberry.misc.toms_storage.filters.ModIdFilter;
import cc.cassian.raspberry.misc.toms_storage.filters.TagFilter;
import cc.cassian.raspberry.misc.toms_storage.filters.TooltipFilter;
import cc.cassian.raspberry.client.tooltips.TooltipCacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.tom.storagemod.gui.AbstractStorageTerminalScreen;
import com.tom.storagemod.gui.PlatformEditBox;
import com.tom.storagemod.gui.StorageTerminalMenu;
import com.tom.storagemod.util.IAutoFillTerminal;
import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Mixin(value = AbstractStorageTerminalScreen.class, remap = false)
public abstract class AbstractStorageTerminalScreenMixin {
    @Shadow
    private PlatformEditBox searchField;

    @Shadow
    private boolean refreshItemList;

    @Shadow
    private String searchLast;

    @Shadow
    private StoredItemStack.IStoredItemStackComparator comparator;

    @Shadow
    private Comparator<StoredItemStack> sortComp;

    @Shadow
    private float currentScroll;

    @Shadow
    private int searchType;

    @Shadow
    protected abstract void onUpdateSearch(String text);

    // The default one will just sit around unused, but it won't take up many resources, so it's fine:tm:
    private static final LoadingCache<StoredItemStack, List<String>> betterTooltipCache =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(Duration.ofSeconds(5))
                    .build(new TooltipCacheLoader());

    @Nullable
    private static Pattern queryToRegex(String regex) {
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

    private static final Predicate<StoredItemStack> DEFAULT_PREDICATE = (stack) -> true;

    @Nonnull
    private static Predicate<StoredItemStack> getStoredItemStackPredicate(String queryPart) {
        Pattern simplePattern = queryToRegex(queryPart);
        Pattern prefixPattern = queryToRegex(queryPart.substring(1));

        Predicate<StoredItemStack> newPredicate;
        if (queryPart.startsWith("@")) {
            newPredicate = new ModIdFilter(prefixPattern);
        } else if (queryPart.startsWith("#")) {
            newPredicate = new TagFilter(prefixPattern);
        } else if (queryPart.startsWith("$")) {
            newPredicate = new TooltipFilter(betterTooltipCache, prefixPattern);
        } else {
            newPredicate = new AnyFilter(betterTooltipCache, simplePattern);
        }
        return newPredicate;
    }

    /* This is extracted from the tail end of the original updateSearch method. We can't feasibly
     * continue there again though, and I really don't want the default search to run in the background
     * unused, so it's just copied wholesale into here.
     */
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

        Predicate<StoredItemStack> predicate = Arrays.stream(query.split(" "))
                .filter(part -> !part.isEmpty())
                .map(String::toLowerCase)
                .map(AbstractStorageTerminalScreenMixin::getStoredItemStackPredicate)
                .reduce(DEFAULT_PREDICATE, Predicate::and);

        @SuppressWarnings("DataFlowIssue")
        var screen = (AbstractStorageTerminalScreen<? extends StorageTerminalMenu>) (Object) this;
        StorageTerminalMenu menu = screen.getMenu();

        menu.itemListClientSorted = menu.itemListClient
                .stream()
                .filter(predicate)
                .sorted(menu.noSort ? this.sortComp : this.comparator)
                .collect(Collectors.toCollection(ArrayList::new));

        if (!this.searchLast.equals(query)) {
            this.resetScroll(menu, query);
        } else {
            menu.scrollTo(this.currentScroll);
        }

        this.refreshItemList = false;
        this.searchLast = query;
    }
}
