package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.RaspberryMod;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class EmiEtchingRecipe extends EmiSmithingRecipe {

    public EmiEtchingRecipe(EmiIngredient input1, EmiStack input2, EmiStack output, ResourceLocation id) {
        super(input1, input2, output, id);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiCompat.ANVIL;
    }

    static void addRunes(EmiRegistry emiRegistry) {
        for (String dye : ALL_RUNES) {
            var enchantedGear = new ItemStack(Items.DIAMOND_CHESTPLATE);
            enchantedGear.enchant(Enchantments.PROJECTILE_PROTECTION, 1);
            var runedGear = enchantedGear.copy();
            var compound = runedGear.getOrCreateTag();
            var quark = new CompoundTag();
            var rune = BuiltInRegistries.ITEM.get(new ResourceLocation("quark", "%s_rune".formatted(dye)));
            quark.putString("id", "quark:%s_rune".formatted(dye));
            quark.putByte("Count", Byte.parseByte("64"));
            compound.put("quark:RuneColor", quark);
            compound.putByte("quark:RuneAttached", Byte.parseByte("1"));
            runedGear.setTag(compound);
            emiRegistry.addRecipe(new EmiEtchingRecipe(
                    EmiStack.of(enchantedGear),
                    EmiStack.of(new ItemStack(rune)),
                    EmiStack.of(runedGear),
                    RaspberryMod.locate("/etching/"+dye)
            ));
        }
    }

    private final static String[] ALL_RUNES = {
            "white",
            "light_gray",
            "gray",
            "black",
            "brown",
            "red",
            "orange",
            "yellow",
            "lime",
            "green",
            "cyan",
            "light_blue",
            "blue",
            "purple",
            "magenta",
            "pink",
            "rainbow"
    };




}
