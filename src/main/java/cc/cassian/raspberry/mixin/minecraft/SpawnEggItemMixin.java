package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.FoliageColor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {
    @WrapMethod(method = "getColor")
    private int mixin(int tintIndex, Operation<Integer> original) {
        if (ModConfig.get().disableSpawnEggTinting)
            return 0xffffff;
        return original.call(tintIndex);
    }
}
