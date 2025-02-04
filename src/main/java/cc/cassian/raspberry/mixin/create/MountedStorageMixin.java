package cc.cassian.raspberry.mixin.create;

import cc.cassian.raspberry.compat.AnotherFurnitureCompat;
import cc.cassian.raspberry.compat.FarmersDelightCompat;
import cc.cassian.raspberry.compat.SupplementariesCompat;
import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(MountedStorage.class)
public class MountedStorageMixin {
    @Inject(method = "canUseAsStorage", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private static void moddedBlocksAreStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        var modlist = ModList.get();
        if (modlist.isLoaded("farmersdelight"))
            if (FarmersDelightCompat.isCabinet(be))
                cir.setReturnValue(true);
        else if (modlist.isLoaded("another_furniture"))
            if (AnotherFurnitureCompat.isDrawer(be))
                cir.setReturnValue(true);
        else if (modlist.isLoaded("supplementaries"))
            if (SupplementariesCompat.isStorage(be))
                cir.setReturnValue(true);
    }
}
