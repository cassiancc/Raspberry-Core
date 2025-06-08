package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModHelpers;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin {
    @WrapMethod(
            method = "getEatingSound")
    private SoundEvent customEatingSounds(Operation<SoundEvent> original) {
        var item = (Item) (Object) this;
        return ModHelpers.getSoundForItem(item.getDefaultInstance(), original.call());
    }
    @WrapMethod(
            method = "getDrinkingSound")
    private SoundEvent customDrinkingSounds(Operation<SoundEvent> original) {
        var item = (Item) (Object) this;
        return ModHelpers.getSoundForItem(item.getDefaultInstance(), original.call());
    }
}
