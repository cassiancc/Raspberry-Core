package cc.cassian.raspberry.mixin.atmospheric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.atmospheric.common.block.AloeVeraBlock;
import com.teamabnormals.atmospheric.common.block.AloeVeraTallBlock;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AloeVeraTallBlock.class)
public class TallAloeVeraBlockMixin {
	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
	private static ItemStack allShearsAreShears(Player instance, InteractionHand interactionHand, Operation<ItemStack> original) {
		if (instance.getItemInHand(interactionHand).is(Tags.Items.SHEARS)) {return Items.SHEARS.getDefaultInstance();}
		return original.call(instance, interactionHand);
	}
}
