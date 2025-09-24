package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.items.AshballItem;
import cc.cassian.raspberry.items.RoseGoldBombItem;
import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
import com.starfish_studios.naturalist.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.registry.NaturalistRegistry;
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

    public static Supplier<Item> ASHBALL = registerBlock("ashball", () -> new AshballItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
    public static Supplier<Item> FIREFLY = registerBlock("firefly", () -> new CaughtMobItem(NaturalistEntityTypes.FIREFLY, ()-> Fluids.EMPTY, NaturalistSoundEvents.SNAIL_FORWARD, new Item.Properties().tab(Naturalist.TAB)));
    public static Supplier<Item> ROSE_GOLD_BOMB = registerBlock("rose_gold_bomb", () -> new RoseGoldBombItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));

    public static RegistryObject<Item> registerBlock(String blockID, Supplier<Item> item) {
        return RaspberryItems.ITEMS.register(blockID, item);
    }
}
