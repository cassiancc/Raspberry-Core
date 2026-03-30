package cc.cassian.raspberry.mixin.autumnity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.autumnity.common.block.LargePumpkinSliceBlock;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LargePumpkinSliceBlock.class)
public class LargePumpkinSliceBlockMixin {
	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean respectModdedShearsPlease(ItemStack instance, TagKey<Item> tag, Operation<Boolean> original) {
		if (instance.is(Tags.Items.SHEARS)) return true;
		return original.call(instance, tag);
	}
}
