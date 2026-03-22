package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.sammy.minersdelight.setup.MDItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class MinersDelightCompat {
	public static void infestedInteract(PlayerInteractEvent.EntityInteract event) {
		var infestedKey = new ResourceLocation("kubejs", "infested");
		if (ForgeRegistries.MOB_EFFECTS.containsKey(infestedKey) && event.getItemStack().is(MDItems.SILVERFISH_EGGS.get()) && event.getTarget() instanceof LivingEntity livingEntity && livingEntity.isAffectedByPotions() && livingEntity.isAlive() && !livingEntity.getType().is(RaspberryTags.INSECTS)) {
			Player player = event.getEntity();
			player.swing(event.getHand(), true);
			if (!player.isCreative()) {
				event.getItemStack().setCount(event.getItemStack().getCount() - 1);
			}
			event.setResult(Event.Result.DENY);
			livingEntity.addEffect(new MobEffectInstance(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getValue(infestedKey)), 200, 0, false, true, true));
		}
	}
}
