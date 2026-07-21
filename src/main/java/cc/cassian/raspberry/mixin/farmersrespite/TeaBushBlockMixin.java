package cc.cassian.raspberry.mixin.farmersrespite;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import umpaz.farmersrespite.common.block.TeaBushBlock;
import vectorwing.farmersdelight.common.tag.ForgeTags;

@Mixin(TeaBushBlock.class)
public class TeaBushBlockMixin {
	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"))
	private Item mixin(ItemStack instance, Operation<Item> original) {
		if (instance.getItem() instanceof ShearsItem) return Items.SHEARS;
		return original.call(instance);
	}
}
