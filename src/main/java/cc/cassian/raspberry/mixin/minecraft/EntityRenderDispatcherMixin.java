package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.client.renderer.LeashFilteringBufferSource;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Unique
    private static final Map<Class<?>, Boolean> raspberry$GECKO_CACHE = new ConcurrentHashMap<>();

    @ModifyVariable(
        method = "render",
        at = @At("HEAD"),
        argsOnly = true
    )
    private MultiBufferSource raspberry$filterGeckoLeash(MultiBufferSource buffer, Entity entity) {
        if (!ModConfig.get().backportLeash) {
            return buffer;
        }

        boolean isGeckoLib = raspberry$GECKO_CACHE.computeIfAbsent(entity.getClass(), clazz -> {
            Class<?> current = clazz;
            while (current != null) {
                for (Class<?> iface : current.getInterfaces()) {
                    if (iface.getName().equals("software.bernie.geckolib3.core.IAnimatable")) {
                        return true;
                    }
                }
                current = current.getSuperclass();
            }
            return false;
        });

        if (isGeckoLib) {
            return new LeashFilteringBufferSource(buffer);
        }

        return buffer;
    }
}