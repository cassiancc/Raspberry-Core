package cc.cassian.raspberry.mixin.create;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FanProcessing.class)
public class FanProcessingMixin {
    @Inject(method = "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"), remap = false, cancellable = true)
    private static void mixin(ItemEntity entity, FanProcessingType type, CallbackInfoReturnable<Boolean> cir) {
        List<ItemStack> stacks = type.process(entity.getItem(), entity.level);
        try {
            assert stacks != null;
            stacks.remove(0);
        } catch (Exception exception) {
            cir.setReturnValue(false);
        }
    }
}
