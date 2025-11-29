package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.toms_storage.tooltips.TooltipCacheLoader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class SilenceServerMixin {
    @WrapOperation(method = "addEntity", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private static void removeWarning(Logger instance, String s, Object object, Operation<Void> original) {
        if (!FMLEnvironment.production) {
            original.call(instance, s, object);
        }
    }
}
