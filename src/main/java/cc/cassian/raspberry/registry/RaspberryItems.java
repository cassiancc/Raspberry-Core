package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.items.AshballItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import oshi.util.tuples.Pair;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static Supplier<Item> ASHBALL = registerBlock("ashball", () -> new AshballItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));

    public static RegistryObject<Item> registerBlock(String blockID, Supplier<Item> item) {
        return RaspberryItems.ITEMS.register(blockID, item);
    }
}
