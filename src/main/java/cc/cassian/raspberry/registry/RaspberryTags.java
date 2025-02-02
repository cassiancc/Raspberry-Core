package cc.cassian.raspberry.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryTags {
    public static final TagKey<Item> DISABLED = createItemTag("disabled");


    public static TagKey<Item> createItemTag(String id) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MOD_ID, id));
    }
}
