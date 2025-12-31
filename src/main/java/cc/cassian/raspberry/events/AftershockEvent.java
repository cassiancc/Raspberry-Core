package cc.cassian.raspberry.events;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.registry.RaspberryMobEffects;
import cc.cassian.raspberry.registry.RaspberryTags;
import cofh.core.init.CoreMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;

public class AftershockEvent {

    /**
     * Adds aftershock to Copper Armor.
     * Implemented via Forge event and mixin into CoFH Core.
     */
    public static void electrify(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        int copperCount = 0;
        if (entity instanceof LivingEntity player) {
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.is(RaspberryTags.COPPER_ARMOR)) {
                    copperCount++;
                }
            }
            copperCount--;
            if (copperCount >= 0) {
                if (!ModCompat.hasCofhCore())
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, copperCount, false, false, false));
                player.addEffect(new MobEffectInstance(RaspberryMobEffects.AFTERSHOCK.get(), 6000, copperCount, false, false, true));
            }
        }
    }

    /**
     * Adds lightning resistance to Copper Armor.
     * Requires CoFH Core.
     * Implemented via mixin.
     */
    public static void resist(LivingEntity entity) {
        int copperCount = 0;
        if (entity instanceof Player player && player.isLocalPlayer()) {
            return;
        }
        for (ItemStack armorSlot : entity.getArmorSlots()) {
            if (armorSlot.is(RaspberryTags.COPPER_ARMOR)) {
                copperCount++;
            }
        }
        copperCount--;
        if (copperCount >= 0) {
            entity.addEffect(new MobEffectInstance(CoreMobEffects.LIGHTNING_RESISTANCE.get(), 200, 0, false, false, true));
        }
    }
}
