package cc.cassian.raspberry.compat.emi;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EmiBeaconPaymentRecipe extends EmiBeaconBaseRecipe {

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiCompat.BEACON_PAYMENT;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return new ResourceLocation("raspberry", "/beacon_payment");
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return BLOCKS;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(getInputs().get(0), 54, 0);
        int x = 0;
        int y = 25;
        int i = 1;
        for (Map.Entry<String, Integer> s : BEACON_PAYMENTS.entrySet()) {
            widgets.addSlot(ingredient(s.getKey()), x, y).recipeContext(this).appendTooltip(Component.translatable("emi.raspberry.beacon_payment.amplifier", s.getValue()).withStyle(ChatFormatting.YELLOW));
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
        emiRegistry.addRecipe(new EmiBeaconPaymentRecipe());
    }

    public static final Map<String, Integer> BEACON_PAYMENTS = Map.ofEntries(
            Map.entry( "additionaladditions:rose_gold_alloy", 45),
            Map.entry("alloyed:bronze_ingot", 45),
            Map.entry( "alloyed:steel_ingot", 45),
            Map.entry( "caverns_and_chasms:necromium_ingot", 125),
            Map.entry( "create:andesite_alloy", 0),
            Map.entry( "create:brass_ingot", 45),
            Map.entry( "create:zinc_ingot", 15),
            Map.entry( "minecraft:amethyst_shard", 7),
            Map.entry( "minecraft:copper_ingot", 0),

            Map.entry("minecraft:diamond", 60),
            Map.entry( "minecraft:echo_shard", 75),
            Map.entry( "minecraft:ender_pearl", 60),
            Map.entry( "minecraft:gold_ingot", 30),

            Map.entry( "minecraft:iron_ingot", 15),

            Map.entry("minecraft:netherite_ingot", 150),
            Map.entry( "oreganized:electrum_ingot", 100),
            Map.entry("oreganized:lead_ingot", 15),
            Map.entry( "oreganized:silver_ingot", 85),

            Map.entry("spelunkery:cinnabar", 7)
            );

    private static final List<EmiIngredient> BLOCKS;

    static {
        ArrayList<EmiIngredient> blocks = new ArrayList<>();
        Set<String> strings = BEACON_PAYMENTS.keySet();
        for (String string : strings) {
            Item value = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(string));
            if (value != null) {
                blocks.add(EmiIngredient.of(Ingredient.of(value.getDefaultInstance())));
            }
        }
        BLOCKS = blocks;

    }
}
