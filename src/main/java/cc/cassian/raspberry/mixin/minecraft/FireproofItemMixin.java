package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class FireproofItemMixin {

    @Shadow
    public abstract ItemStack getDefaultInstance();

    @WrapMethod(method = "isFireResistant")
    private boolean fireproofTag(Operation<Boolean> original) {
        return original.call() || this.getDefaultInstance().is(RaspberryTags.FIREPROOF);
    }
}
