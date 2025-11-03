package cc.cassian.raspberry.events;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DarknessRepairEvent {
    public static void tick(Player player) {
        if (player.getRandom().nextInt(101)>99) {
            if (player.level.getMaxLocalRawBrightness(player.getOnPos())<1 || player.hasEffect(MobEffects.DARKNESS) || player.hasEffect(MobEffects.BLINDNESS)) {
                if (player.getInventory().hasAnyMatching(itemStack -> itemStack.is(RaspberryTags.REPAIRS_IN_DARKNESS))) {
                    for (ItemStack item : player.getInventory().items) {
                        if (item.is(RaspberryTags.REPAIRS_IN_DARKNESS)) {
                            item.setDamageValue(item.getDamageValue()-1);
                        }
                    }
                }
            }
        }
    }
}
