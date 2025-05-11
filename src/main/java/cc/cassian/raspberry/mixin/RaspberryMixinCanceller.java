package cc.cassian.raspberry.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class RaspberryMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("me.juancarloscp52.spyglass_improvements.mixin.MinecraftMixin")) {
            return true;
        }
        return false;
    }
}
