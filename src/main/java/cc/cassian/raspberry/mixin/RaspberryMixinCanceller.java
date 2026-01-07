package cc.cassian.raspberry.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import net.minecraftforge.fml.loading.LoadingModList;

import java.util.List;

public class RaspberryMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("me.juancarloscp52.spyglass_improvements.mixin.MinecraftMixin")) {
            return true;
        }
        else if (mixinClassName.equals("com.mrcrayfish.controllable.mixin.client.jei.IngredientListOverlayMixin") && LoadingModList.get().getModFileById("emi") != null) { 
            return true;
        }
        else if (mixinClassName.equals("org.embeddedt.modernfix.common.mixin.perf.blast_search_trees.MinecraftMixin")) {
            return true;
        }
        return false;
    }
}
