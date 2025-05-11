package cc.cassian.raspberry.mixin;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.mapatlases.map_collection.MapCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Pseudo
@Mixin(MapCollection.class)
@Debug(export = true)
public abstract class MapCollectionMixin {
    @Shadow @Final private Set<Integer> ids;

    @Inject(method = "serializeNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;<init>()V"), cancellable = true)
    private void mixin(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag c = new CompoundTag();
        ArrayList<Integer> test = new ArrayList<>(ids);
        c.putIntArray("maps", test);
        cir.setReturnValue(c);
        cir.cancel();
    }
}
