package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.registry.RasperryMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.onvoid.copperized.common.CopperizedArmorMaterials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CopperizedCompat {
    private static final Logger log = LogManager.getLogger(CopperizedCompat.class);

    public static void electrify(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        int copperCount = 0;
        if (entity instanceof Player player) {
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(CopperizedArmorMaterials.COPPER)) {
                    copperCount++;
                }
            }
            copperCount--;
            if (copperCount >= 0) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, copperCount, false, false, false));
                player.addEffect(new MobEffectInstance(RasperryMobEffects.THUNDER_SYNERGY.get(), 200, copperCount, false, false, true));
            }
        }


    }
}
