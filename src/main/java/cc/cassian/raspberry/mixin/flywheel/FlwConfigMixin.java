package cc.cassian.raspberry.mixin.flywheel;

import com.jozufozu.flywheel.config.BackendType;
import com.jozufozu.flywheel.config.FlwConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = FlwConfig.class, remap = false)
public class FlwConfigMixin {
    
    @Shadow
    public FlwConfig.ClientConfig client;
    
    @Inject(method = "getBackendType", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$fixEarlyConfigAccess(CallbackInfoReturnable<BackendType> cir) {
        try {
            BackendType type = client.backend.get();
            cir.setReturnValue(type);
        } catch (IllegalStateException e) {
            cir.setReturnValue(BackendType.INSTANCING);
        }
    }
    
    @Inject(method = "debugNormals", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$fixDebugNormalsEarlyAccess(CallbackInfoReturnable<Boolean> cir) {
        try {
            cir.setReturnValue(client.debugNormals.get());
        } catch (IllegalStateException e) {
            cir.setReturnValue(false);
        }
    }
    
    @Inject(method = "limitUpdates", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$fixLimitUpdatesEarlyAccess(CallbackInfoReturnable<Boolean> cir) {
        try {
            cir.setReturnValue(client.limitUpdates.get());
        } catch (IllegalStateException e) {
            cir.setReturnValue(true);
        }
    }
}