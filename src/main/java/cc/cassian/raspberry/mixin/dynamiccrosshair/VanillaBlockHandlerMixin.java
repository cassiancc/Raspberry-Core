package cc.cassian.raspberry.mixin.dynamiccrosshair;

import cc.cassian.raspberry.compat.QuarkCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.dynamiccrosshair.handler.VanillaBlockHandler;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(VanillaBlockHandler.class)
public class VanillaBlockHandlerMixin {

    @WrapOperation(
            method = "checkToolWithBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private static boolean bypassExpensiveCalculationIfNecessary(Item instance, BlockState block, Operation<Boolean> original) {
        if (ModList.get().isLoaded("quark") && instance instanceof DiggerItem diggerItem && diggerItem.getTier().equals(Tiers.GOLD)) {
            if (QuarkCompat.checkGold(diggerItem, block))
                return true;
        }
        return original.call(instance, block);
    }
}
