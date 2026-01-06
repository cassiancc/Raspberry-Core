package cc.cassian.raspberry.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.search.EmiSearch;
import net.minecraft.client.searchtree.RefreshableSearchTree;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.embeddedt.modernfix.searchtree.DummySearchTree;
import org.embeddedt.modernfix.searchtree.SearchTreeProviderRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Uses EMI to handle search tree lookups.
 * Includes caching and tag-search handling to support ModernFix's creative search optimization.
 */
public class EmiBackedSearchTree extends DummySearchTree<ItemStack> {
    private final boolean filteringByTag;

    private volatile String lastSearchText = null;
    private volatile List<ItemStack> lastResult = Collections.emptyList();

    public EmiBackedSearchTree(boolean filteringByTag) {
        this.filteringByTag = filteringByTag;
    }

    @Override
    public @NotNull List<ItemStack> search(String searchText) {
        String cachedText = lastSearchText;
        if (searchText.equals(cachedText)) {
            return new ArrayList<>(lastResult);
        }

        String query = filteringByTag && !searchText.startsWith("#")
                ? "#" + searchText
                : searchText;

        EmiSearch.CompiledQuery compiledQuery;
        try {
            compiledQuery = new EmiSearch.CompiledQuery(query);
        } catch (Exception e) {
            return Collections.emptyList();
        }

        List<ItemStack> results = new ArrayList<>();
        for (EmiStack stack : EmiApi.getIndexStacks()) {
            if (compiledQuery.test(stack)) {
                ItemStack itemStack = stack.getItemStack();
                if (itemStack != null && !itemStack.isEmpty()) {
                    results.add(itemStack);
                }
            }
        }

        lastSearchText = searchText;
        lastResult = results;

        return new ArrayList<>(results);
    }

    public static final SearchTreeProviderRegistry.Provider PROVIDER = new SearchTreeProviderRegistry.Provider() {
        @Override
        public RefreshableSearchTree<ItemStack> getSearchTree(boolean tag) {
            return new EmiBackedSearchTree(tag);
        }

        @Override
        public boolean canUse() {
            return ModList.get().isLoaded("emi");
        }

        @Override
        public String getName() {
            return "EMI";
        }
    };
}