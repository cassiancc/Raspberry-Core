package cc.cassian.raspberry.compat.toms_storage.filters;

import com.google.common.cache.LoadingCache;
import com.tom.storagemod.util.StoredItemStack;
import dev.emi.emi.config.EmiConfig;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AnyFilter implements Predicate<StoredItemStack> {
    private final NameFilter nameFilter;
    private final ModIdFilter modIdFilter;
    private final TagFilter tagFilter;
    private final TooltipFilter tooltipFilter;

    public AnyFilter(LoadingCache<StoredItemStack, List<String>> tooltipCache, Pattern pattern) {
        this.nameFilter = new NameFilter(pattern);

        this.modIdFilter = EmiConfig.searchModNameByDefault ? new ModIdFilter(pattern) : null;
        this.tagFilter = EmiConfig.searchTagsByDefault ? new TagFilter(pattern) : null;
        this.tooltipFilter = EmiConfig.searchTooltipByDefault ? new TooltipFilter(tooltipCache, pattern) : null;
    }

    @Override
    public boolean test(StoredItemStack storedItemStack) {
        if (nameFilter.test(storedItemStack)) return true;

        if (modIdFilter != null && modIdFilter.test(storedItemStack)) return true;
        if (tagFilter != null && tagFilter.test(storedItemStack)) return true;
        return tooltipFilter != null && tooltipFilter.test(storedItemStack);
    }
}
