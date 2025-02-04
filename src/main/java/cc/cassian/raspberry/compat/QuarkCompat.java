package cc.cassian.raspberry.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.content.tweaks.module.GoldToolsHaveFortuneModule;

import java.util.Locale;

public class QuarkCompat {
    public static boolean checkGold(Item diggerItem, BlockState block) {
        if (GoldToolsHaveFortuneModule.harvestLevel != 0) {
            Tiers[] values = Tiers.values();
            for (Tiers value : values) {
                if (value.getLevel() == GoldToolsHaveFortuneModule.harvestLevel) {
                    ResourceLocation key = ForgeRegistries.ITEMS.getKey(diggerItem);
                    ResourceLocation effectiveKey = new ResourceLocation(key.getNamespace(), key.getPath().replace("golden", value.toString().toLowerCase(Locale.ROOT)));
                    Item item = ForgeRegistries.ITEMS.getValue(effectiveKey);
                    if (item != null)
                        return item.isCorrectToolForDrops(item.getDefaultInstance(), block);
                }
            }
        }

        return false;
    }
}
