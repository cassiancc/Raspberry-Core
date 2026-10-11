package cc.cassian.raspberry.mixin.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.item_obliterator.ItemObliteratorCompat;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.emi.emi.EmiPort;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.stream.Stream;

@Mixin(EmiPort.class)
public class VanillaPluginMixin {
    @ModifyReturnValue(method = "getDisabledItems", at = @At(value = "RETURN"), remap = false)
    private static Stream<Item> mixin(Stream<Item> original) {
        if (!ModCompat.hasItemObliterator()) return original;
        var list = new ArrayList<>(original.toList());
        list.addAll(ItemObliteratorCompat.getBlacklistedItems());
        return list.stream();
    }
}
