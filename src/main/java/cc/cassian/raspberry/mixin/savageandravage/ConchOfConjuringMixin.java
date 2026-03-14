package cc.cassian.raspberry.mixin.savageandravage;

import cc.cassian.raspberry.events.ChangeBlockEvent;
import com.teamabnormals.savage_and_ravage.common.item.ConchOfConjuringItem;
import com.teamabnormals.savage_and_ravage.core.registry.SRBlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConchOfConjuringItem.class)
public class ConchOfConjuringMixin {
	@Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
	private void raspberry_cycleBlock(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (ChangeBlockEvent.changeBlock(level, player, hand, SRBlocks.GLOOMY_TILES.get(), SRBlocks.RUNED_GLOOMY_TILES.get())) {
			cir.setReturnValue(InteractionResultHolder.success(player.getItemInHand(hand)));
		}
	}
}
