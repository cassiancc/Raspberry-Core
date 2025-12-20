package cc.cassian.raspberry.client.registry;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class RaspberryItemProperties {

    private static final ItemPropertyFunction FLOWER_GARLAND_EQUIPPED = (stack, level, entity, i) -> {
        if (entity == null) {
            return 0.0F;
        } else {
            return entity.getItemBySlot(EquipmentSlot.HEAD) == stack ? 1.0F : 0.0F;
        }
    };

    public static void register() {
        ItemProperties.register(RaspberryItems.CHEERY_WILDFLOWER_GARLAND.get(), RaspberryMod.locate("equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.HOPEFUL_WILDFLOWER_GARLAND.get(), RaspberryMod.locate("equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.PLAYFUL_WILDFLOWER_GARLAND.get(), RaspberryMod.locate("equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(RaspberryItems.MOODY_WILDFLOWER_GARLAND.get(), RaspberryMod.locate( "equipped"), FLOWER_GARLAND_EQUIPPED);
        ItemProperties.register(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("kubejs", "copper_brush_wax")), new ResourceLocation("selecting"), (stack, level, entity, i)->{
            return stack.getOrCreateTag().contains("pos") ? 1 : 0;
        });
    }
}