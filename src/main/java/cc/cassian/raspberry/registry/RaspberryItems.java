package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.items.AshballItem;
import cc.cassian.raspberry.items.CatFoodItem;
import cc.cassian.raspberry.items.FlowerGarlandItem;
import cc.cassian.raspberry.items.RoseGoldBombItem;
import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
import com.starfish_studios.naturalist.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.registry.NaturalistSoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static Supplier<Item> ASHBALL = registerItem("ashball", () -> new AshballItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static Supplier<Item> FIREFLY = registerItem("firefly", () -> new CaughtMobItem(NaturalistEntityTypes.FIREFLY, ()-> Fluids.EMPTY, NaturalistSoundEvents.SNAIL_FORWARD, new Item.Properties().tab(Naturalist.TAB)));
    public static Supplier<Item> ROSE_GOLD_BOMB = registerItem("rose_gold_bomb", () -> new RoseGoldBombItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static Supplier<Item> CAT_FOOD = registerItem("cat_food", () -> new CatFoodItem(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

    public static Supplier<Item> CHEERY_WILDFLOWER_GARLAND = registerItem("cheery_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.CHEERY_WILDFLOWER_GARLAND.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static Supplier<Item> HOPEFUL_WILDFLOWER_GARLAND = registerItem("hopeful_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.HOPEFUL_WILDFLOWER_GARLAND.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static Supplier<Item> PLAYFUL_WILDFLOWER_GARLAND = registerItem("playful_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.PLAYFUL_WILDFLOWER_GARLAND.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    public static Supplier<Item> MOODY_WILDFLOWER_GARLAND = registerItem("moody_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.MOODY_WILDFLOWER_GARLAND.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    public static RegistryObject<Item> registerItem(String itemID, Supplier<Item> item) {
        return RaspberryItems.ITEMS.register(itemID, item);
    }
}
