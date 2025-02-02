package cc.cassian.raspberry.mixin;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RaspberryMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (checkMixin("environmental", mixinClassName)){
            return false;
        }
        else if (checkMixin("create", mixinClassName)){
            return false;
        }
        else if (checkMixin("farmersdelight", mixinClassName)){
            return false;
        }
        else if (checkMixin("naturalist", mixinClassName)){
            return false;
        }
        else if (checkMixin("upgrade_aquatic", mixinClassName)){
            return false;
        }
        else if (checkMixin("sullysmod", mixinClassName)){
            return false;
        }
        else if (checkMixin("caverns_and_chasms", mixinClassName)){
            return false;
        }
        return true;
    }

    public static boolean checkMixin(String modID, String mixinClassName) {
        var modList = LoadingModList.get();
        return (mixinClassName.contains("cc.cassian.raspberry.mixin."+modID) && modList.getModFileById(modID) == null);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
