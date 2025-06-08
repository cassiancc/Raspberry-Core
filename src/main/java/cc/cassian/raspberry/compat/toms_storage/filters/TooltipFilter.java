package cc.cassian.raspberry.compat.toms_storage.filters;

import com.google.common.cache.LoadingCache;
import com.tom.storagemod.util.StoredItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TooltipFilter implements Predicate<StoredItemStack> {
    private final Pattern pattern;
    private final LoadingCache<StoredItemStack, List<String>> tooltipCache;

    public TooltipFilter(LoadingCache<StoredItemStack, List<String>> tooltipCache, Pattern pattern) {
        this.tooltipCache = tooltipCache;
        this.pattern = pattern;
    }

    @Nonnull
    private List<String> lookupTooltips(StoredItemStack stack) {
        List<String> tooltips;
        try {
            tooltips = tooltipCache.get(stack);
        } catch (ExecutionException e) {
            return List.of();
        }

        // The first line of a tooltip will be the name of the item,
        // the second line is empty. (Apparently? EMI assumes it is.)
        // To have some kind of parity to EMI, we consider the item
        // name *not* to be part of the tooltip!
        return tooltips.isEmpty() ? tooltips : tooltips.subList(1, tooltips.size());
    }

    @Override
    public boolean test(StoredItemStack storedItemStack) {
        List<String> tooltips = lookupTooltips(storedItemStack);
        return tooltips.stream().anyMatch(tooltip -> pattern.matcher(tooltip.toLowerCase()).find());
    }
}
