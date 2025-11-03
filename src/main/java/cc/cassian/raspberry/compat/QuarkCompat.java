package cc.cassian.raspberry.compat;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.violetmoon.quark.addons.oddities.capability.CrateItemHandler;
import org.violetmoon.quark.content.building.block.PaperLanternBlock;
import org.violetmoon.quark.content.tweaks.module.GoldToolsHaveFortuneModule;
import java.util.Locale;

import static cc.cassian.raspberry.RaspberryMod.identifier;

public class QuarkCompat {
    public static boolean checkGold(Item diggerItem, BlockState block) {
        if (GoldToolsHaveFortuneModule.harvestLevel != 0) {
            Tiers[] values = Tiers.values();
            for (Tiers value : values) {
                if (value.getLevel() == GoldToolsHaveFortuneModule.harvestLevel) {
                    ResourceLocation key = ForgeRegistries.ITEMS.getKey(diggerItem);
                    ResourceLocation effectiveKey = identifier(key.getNamespace(), key.getPath().replace("golden", value.toString().toLowerCase(Locale.ROOT)));
                    Item item = ForgeRegistries.ITEMS.getValue(effectiveKey);
                    if (item != null)
                        return item.isCorrectToolForDrops(item.getDefaultInstance(), block);
                }
            }
        }

        return false;
    }

    public static boolean isPaperLantern(BlockState downState) {
        return downState.getBlock() instanceof PaperLanternBlock;
    }

    public static boolean isCrateItemHandler(ItemStackHandler obj) {
        return obj instanceof CrateItemHandler;
    }

    public static void register() {
     
    }
}
