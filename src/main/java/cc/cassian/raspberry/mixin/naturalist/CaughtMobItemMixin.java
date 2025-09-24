package cc.cassian.raspberry.mixin.naturalist;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(CaughtMobItem.class)
public class CaughtMobItemMixin {
    @WrapMethod(method = "getEmptySuccessItem", remap = false)
    private static ItemStack stackableButterflies(ItemStack bucketStack, Player player, Operation<ItemStack> original) {
        if (player.getAbilities().instabuild || bucketStack.getCount() == 1) {
            return original.call(bucketStack, player);
        }
        var newStack = bucketStack.copy();
        newStack.setCount(bucketStack.getCount()-1);
        return newStack;
    }
}
