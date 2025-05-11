package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.RaspberryMod;
import cofh.ensorcellation.init.EnsorcEnchantments;
import com.brokenkeyboard.usefulspyglass.UsefulSpyglass;
import com.github.alexthe668.domesticationinnovation.server.enchantment.DIEnchantmentRegistry;
import com.github.alexthe668.domesticationinnovation.server.item.DIItemRegistry;
import com.simibubi.create.AllItems;
import com.teamabnormals.allurement.core.registry.AllurementEnchantments;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.miningmaster.init.MMEnchantments;
import vectorwing.farmersdelight.common.registry.ModEnchantments;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

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
            var rune = Registry.ITEM.get(new ResourceLocation("quark", "%s_rune".formatted(dye)));
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
