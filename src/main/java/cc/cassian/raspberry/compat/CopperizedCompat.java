package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.registry.RasperryMobEffects;
import cofh.core.init.CoreMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
//import net.onvoid.copperized.common.CopperizedArmorMaterials;
import net.minecraftforge.fml.ModList;
import fr.anto.bettercopper.utils.CustomArmorMaterials;

public class CopperizedCompat {
    public static final ArmorMaterial COPPER = CustomArmorMaterials.COPPER_ARMOR;

    public static void electrify(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();
        int copperCount = 0;
        if (entity instanceof Player player) {
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                //TODO find new copper armour mod
                if (armorSlot.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(COPPER)) {
                    copperCount++;
                }
            }
            copperCount--;
            if (copperCount >= 0) {
                if (!ModList.get().isLoaded("cofh_core"))
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, copperCount, false, false, false));
                player.addEffect(new MobEffectInstance(RasperryMobEffects.AFTERSHOCK.get(), 6000, copperCount, false, false, true));
            }
        }


    }

    public static void resist(TickEvent.PlayerTickEvent event) {
        Entity entity = event.player;
        int copperCount = 0;
        if (entity instanceof Player player) {
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(COPPER)) {
                    copperCount++;
                }
            }
            copperCount--;
            if (copperCount >= 0) {
                player.addEffect(new MobEffectInstance(CoreMobEffects.LIGHTNING_RESISTANCE.get(), 200, 0, false, false, true));
            }
        }


    }
}
