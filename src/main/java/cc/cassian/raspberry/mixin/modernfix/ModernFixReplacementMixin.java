package cc.cassian.raspberry.mixin.modernfix;

import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.ItemStack;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.platform.ModernFixPlatformHooks;
import org.embeddedt.modernfix.searchtree.RecipeBookSearchTree;
import org.embeddedt.modernfix.searchtree.SearchTreeProviderRegistry;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 1100)
public abstract class ModernFixReplacementMixin {

    @Shadow @Final private SearchRegistry searchRegistry;

    @Shadow public abstract <T> void populateSearchTree(SearchRegistry.Key<T> pKey, List<T> pItems);

    @Inject(method = "createSearchTrees", at = @At("HEAD"), cancellable = true)
    private void replaceSearchTrees(CallbackInfo ci) {
        SearchTreeProviderRegistry.Provider provider = SearchTreeProviderRegistry.getSearchTreeProvider();
        if (provider != null) {
            ModernFix.LOGGER.info("Replacing search trees with '{}' provider (Raspberry Dev Fix)", provider.getName());

            SearchRegistry.TreeBuilderSupplier<ItemStack> nameSupplier = (list) -> provider.getSearchTree(false);
            SearchRegistry.TreeBuilderSupplier<ItemStack> tagSupplier = (list) -> provider.getSearchTree(true);

            this.searchRegistry.register(SearchRegistry.CREATIVE_NAMES, nameSupplier);
            this.searchRegistry.register(SearchRegistry.CREATIVE_TAGS, tagSupplier);
            this.searchRegistry.register(SearchRegistry.RECIPE_COLLECTIONS, (list) -> new RecipeBookSearchTree(provider.getSearchTree(false), list));

            ModernFixPlatformHooks.INSTANCE.registerCreativeSearchTrees(this.searchRegistry, nameSupplier, tagSupplier, this::populateSearchTree);

            GLFWErrorCallback oldCb = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)null);

            for(KeyMapping mapping : KeyMappingAccessor.getAll().values()) {
                mapping.getTranslatedKeyMessage();
            }

            GLFW.glfwSetErrorCallback(oldCb);
            ci.cancel();
        }
    }
}