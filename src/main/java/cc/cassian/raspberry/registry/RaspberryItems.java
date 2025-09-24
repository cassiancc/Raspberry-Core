package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.items.AshballItem;
import cc.cassian.raspberry.items.RoseGoldBombItem;
import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.core.registry.NaturalistSoundEvents;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
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

    public static Supplier<Item> ASHBALL = registerBlock("ashball", () -> new AshballItem(new Item.Properties()));
    public static Supplier<Item> FIREFLY = registerBlock("firefly", () -> new CaughtMobItem(NaturalistEntityTypes.FIREFLY, ()-> Fluids.EMPTY, NaturalistSoundEvents.SNAIL_FORWARD, new Item.Properties()));
    public static Supplier<Item> ROSE_GOLD_BOMB = registerBlock("rose_gold_bomb", () -> new RoseGoldBombItem(new Item.Properties()));

    public static RegistryObject<Item> registerBlock(String blockID, Supplier<Item> item) {
        return RaspberryItems.ITEMS.register(blockID, item);
    }
}
