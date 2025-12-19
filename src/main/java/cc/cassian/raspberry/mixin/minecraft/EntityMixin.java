package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.KnotConnectionAccess;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getEncodeId", at = @At("HEAD"), cancellable = true)
    private void raspberry$getEncodeId(CallbackInfoReturnable<String> cir) {
        if (!ModConfig.get().backportLeash) return;

        if ((Object)this instanceof LeashFenceKnotEntity knot) {

            if (knot instanceof KnotConnectionAccess access && access.raspberry$getConnectionManager().hasConnections()) {
                cir.setReturnValue(EntityType.getKey(knot.getType()).toString());
            } else {
                cir.setReturnValue(null);
            }
        }
    }
}