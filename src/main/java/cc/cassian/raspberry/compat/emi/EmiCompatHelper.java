package cc.cassian.raspberry.compat.emi;

import org.embeddedt.modernfix.searchtree.SearchTreeProviderRegistry;

public class EmiCompatHelper {
    public static void register() {
        SearchTreeProviderRegistry.register(cc.cassian.raspberry.compat.emi.EmiBackedSearchTree.PROVIDER);
    }
}
