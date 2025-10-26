package cc.cassian.raspberry.compat.emi;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EmiBeaconBaseRecipe implements EmiRecipe {

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiCompat.BEACON_BASE;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return new ResourceLocation("raspberry", "/beacon_base");
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return BLOCKS;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 127;
    }

    @Override
    public int getDisplayHeight() {
        return 123;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getInputs().get(0), 54, 0);
        int x = 0;
        int y = 25;
        int i = 1;
        for (Map.Entry<String, Integer> s : BEACON_BASE_BLOCKS.entrySet()) {
            widgets.addSlot(ingredient(s.getKey()), x, y).recipeContext(this).appendTooltip(Component.translatable("emi.raspberry.beacon_base.amplifier", s.getValue()).withStyle(ChatFormatting.YELLOW));
            if (i != 7) {
                x+=18;
            }
            else {
                x = 0;
                y+=18;
                i = 0;
            }
            i++;
        }
    }

    private EmiIngredient ingredient(String s) {
        return ingredient(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s))));
    }

    public static EmiIngredient ingredient(Item item) {
        return EmiIngredient.of(Ingredient.of((item.getDefaultInstance())));

    }

    public static void addBeaconRecipe(EmiRegistry emiRegistry) {
        emiRegistry.addRecipe(new EmiBeaconBaseRecipe());
    }

    public static final LinkedHashMap<String, Integer> BEACON_BASE_BLOCKS = beaconBaseBlocks();

    private static LinkedHashMap<String, Integer> beaconBaseBlocks() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put( "copperandtuffbackport:chiseled_copper", 0);
        map.put("copperandtuffbackport:waxed_chiseled_copper", 0);
        map.put( "copperandtuffbackport:exposed_chiseled_copper", 0);
        map.put( "copperandtuffbackport:waxed_exposed_chiseled_copper", 0);
        map.put( "copperandtuffbackport:weathered_chiseled_copper", 0);
        map.put( "copperandtuffbackport:waxed_weathered_chiseled_copper", 0);
        map.put( "copperandtuffbackport:oxidized_chiseled_copper", 0);
        map.put( "copperandtuffbackport:waxed_oxidized_chiseled_copper", 0);
        map.put( "spelunkery:cinnabar_block", 0);

        map.put("create:zinc_block", 1);
        map.put( "twigs:polished_amethyst", 1);
        map.put( "oreganized:lead_block", 1);
        map.put( "minecraft:iron_block", 1);

        map.put( "minecraft:gold_block", 2);

        map.put("create:brass_block", 3);
        map.put( "minecraft:diamond_block", 3);
        map.put("kubejs:rose_gold_block", 3);
        map.put( "kubejs:bronze_block", 3);

        map.put("oreganized:silver_block", 4);

        map.put("architects_palette:ender_pearl_block", 5);

        map.put("caverns_and_chasms:echo_block", 6);
        map.put( "oreganized:electrum_block", 6);

        map.put( "caverns_and_chasms:necromium_block", 7);
        map.put( "minecraft:netherite_block", 8);
        return map;
    }

    private static final List<EmiIngredient> BLOCKS;

    static {
        ArrayList<EmiIngredient> blocks = new ArrayList<>();
        for (String string : BEACON_BASE_BLOCKS.keySet()) {
            Item value = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(string));
            if (value != null) {
                blocks.add(EmiIngredient.of(Ingredient.of(value.getDefaultInstance())));
            }
        }
        BLOCKS = blocks;
    }
}
