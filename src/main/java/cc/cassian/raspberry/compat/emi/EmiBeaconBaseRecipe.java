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
        return List.of(EmiIngredient.of(Ingredient.of(Items.BEACON)));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return BLOCKS;
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
        widgets.addGeneratedSlot((r) -> getInputs().get(0), 123, 54, 0);
        int x = 0;
        int y = 25;
        int i = 1;
        for (Map.Entry<String, Integer> s : BEACON_BASE_BLOCKS.entrySet()) {
            widgets.addGeneratedSlot((r) -> ingredient(s.getKey()), EmiUtil.RANDOM.nextInt(), x, y).recipeContext(this).appendTooltip(Component.translatable("emi.raspberry.beacon_base.amplifier", s.getValue()).withStyle(ChatFormatting.YELLOW));
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

    public static final Map<String, Integer> BEACON_BASE_BLOCKS = Map.ofEntries(
            Map.entry( "copperandtuffbackport:chiseled_copper", 0),
            Map.entry("copperandtuffbackport:waxed_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:exposed_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:waxed_exposed_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:weathered_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:waxed_weathered_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:oxidized_chiseled_copper", 0),
            Map.entry( "copperandtuffbackport:waxed_oxidized_chiseled_copper", 0),
            Map.entry( "spelunkery:cinnabar_block", 0),

            Map.entry("create:zinc_block", 1),
            Map.entry( "twigs:polished_amethyst", 1),
            Map.entry( "oreganized:lead_block", 1),
            Map.entry( "minecraft:iron_block", 1),

            Map.entry( "minecraft:gold_block", 2),

            Map.entry("create:brass_block", 3),
            Map.entry( "minecraft:diamond_block", 3),
            Map.entry("kubejs:rose_gold_block", 3),
            Map.entry( "kubejs:bronze_block", 3),

            Map.entry("oreganized:silver_block", 4),

            Map.entry("architects_palette:ender_pearl_block", 5),

            Map.entry("caverns_and_chasms:echo_block", 6),
            Map.entry( "oreganized:electrum_block", 6),

            Map.entry( "caverns_and_chasms:necromium_block", 7),
            Map.entry( "minecraft:netherite_block", 8)


            );

    private static final List<EmiStack> BLOCKS;

    static {
        ArrayList<EmiStack> blocks = new ArrayList<>();
        Set<String> strings = BEACON_BASE_BLOCKS.keySet();
        for (String string : strings) {
            Item value = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(string));
            if (value != null) {
                blocks.add(EmiStack.of(value.getDefaultInstance()));
            }
        }
        BLOCKS = blocks;

    }
}
