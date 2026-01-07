package cc.cassian.raspberry.mixin.modernfix;

import cc.cassian.raspberry.compat.emi.EmiBackedSearchTree;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 100)
public class ModernFixCompatMixin {

    @Inject(method = "createSearchTrees", at = @At("HEAD"))
    private void registerEmiSearchTree(CallbackInfo ci) {
        if (ModList.get().isLoaded("emi") && ModList.get().isLoaded("modernfix")) {
            EmiBackedSearchTree.register();
        }
    }
}