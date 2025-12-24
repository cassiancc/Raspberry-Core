package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.NaturalistCompat;
import cc.cassian.raspberry.items.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.FoodValues;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static Supplier<Item> ASHBALL = registerItem("ashball", () -> new AshballItem(new Item.Properties()));
    public static Supplier<Item> FIREFLY = registerFireflyItem();

    private static Supplier<Item> registerFireflyItem() {
        if (ModCompat.NATURALIST) {
            return NaturalistCompat.registerFireflyItem();
        } else {
            return RaspberryItems.registerItem("firefly", () -> new Item(new Item.Properties()));
        }
    }

    public static Supplier<Item> ROSE_GOLD_BOMB = registerItem("rose_gold_bomb", () -> new RoseGoldBombItem(new Item.Properties()));
    public static Supplier<Item> SWAP_ARROW = registerItem("swap_arrow", () -> new SwapArrowItem(new Item.Properties()));
    public static Supplier<Item> CAT_FOOD = registerItem("cat_food", () -> new CatFoodItem(new Item.Properties().craftRemainder(Items.BOWL)));
    public static Supplier<Item> ATLAS = registerItem("atlas", () -> new AtlasItem(new Item.Properties()));
    public static Supplier<Item> CHERRY_PIE_SLICE = registerItem("cherry_pie_slice", () -> new Item(new Item.Properties().food(FoodValues.PIE_SLICE)));

    public static Supplier<Item> CHEERY_WILDFLOWER_GARLAND = registerItem("cheery_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.CHEERY_WILDFLOWER_GARLAND.get(), new Item.Properties()));
    public static Supplier<Item> HOPEFUL_WILDFLOWER_GARLAND = registerItem("hopeful_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.HOPEFUL_WILDFLOWER_GARLAND.get(), new Item.Properties()));
    public static Supplier<Item> PLAYFUL_WILDFLOWER_GARLAND = registerItem("playful_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.PLAYFUL_WILDFLOWER_GARLAND.get(), new Item.Properties()));
    public static Supplier<Item> MOODY_WILDFLOWER_GARLAND = registerItem("moody_wildflower_garland", () -> new FlowerGarlandItem(RaspberryBlocks.MOODY_WILDFLOWER_GARLAND.get(), new Item.Properties()));

    public static RegistryObject<Item> registerItem(String itemID, Supplier<Item> item) {
        return RaspberryItems.ITEMS.register(itemID, item);
    }
}
