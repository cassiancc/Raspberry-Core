package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.BrewinAndChewinCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.events.DripstoneEvent;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.Collections;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static EmiRecipeCategory ANVIL = new EmiRecipeCategory(RaspberryMod.locate("anvil"), EmiStack.of(Items.ANVIL));
    public static EmiRecipeCategory BEACON_BASE = new EmiRecipeCategory(RaspberryMod.locate("beacon_base"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory BEACON_PAYMENT = new EmiRecipeCategory(RaspberryMod.locate("beacon_payment"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory DRIPPING = new EmiRecipeCategory(RaspberryMod.locate("dripping"), EmiStack.of(Items.POINTED_DRIPSTONE));


    @Override
    public void register(EmiRegistry emiRegistry) {
        RaspberryMod.LOGGER.info("Initializing EMI Integration");
        if (ModConfig.get().emi_tablets && ModCompat.CREATE && ModCompat.DOMESTICATION_INNOVATION && ModCompat.ENSORCELLATION && ModCompat.SUPPLEMENTARIES && ModCompat.ALLUREMENT) {
            EmiSmithingRecipe.addEnchantments(emiRegistry);
        }
        if (ModCompat.QUARK) {
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiIngredient.of(Ingredient.of(Items.ANVIL, Items.CHIPPED_ANVIL, Items.DAMAGED_ANVIL)));
            emiRegistry.addCategory(ANVIL);
            EmiEtchingRecipe.addRunes(emiRegistry);
        }
        if (ModCompat.BETTER_BEACONS) {
            emiRegistry.addWorkstation(EmiCompat.BEACON_BASE, EmiStack.of(Items.BEACON));
            emiRegistry.addCategory(BEACON_BASE);
            EmiBeaconBaseRecipe.addBeaconRecipe(emiRegistry);
            emiRegistry.addWorkstation(EmiCompat.BEACON_PAYMENT, EmiStack.of(Items.BEACON));
            emiRegistry.addCategory(BEACON_PAYMENT);
            EmiBeaconPaymentRecipe.addBeaconRecipe(emiRegistry);
        }
        if (ModCompat.BREWINANDCHEWIN) {
            BrewinAndChewinCompat.registerEmi(emiRegistry);
        }
        emiRegistry.addCategory(DRIPPING);
        emiRegistry.addWorkstation(DRIPPING, EmiDripstoneRecipe.POINTED_DRIPSTONE);

        if (ModConfig.get().saltGenerator) {
            emiRegistry.addRecipe(new EmiDripstoneRecipe(DripstoneEvent.SALT));
        }

        if (ModConfig.get().corundumGenerators) {
            emiRegistry.addRecipe(new EmiDripstoneRecipe(DripstoneEvent.REDSTONE));
            emiRegistry.addRecipe(new EmiDripstoneRecipe(DripstoneEvent.QUARTZ));
            emiRegistry.addRecipe(new EmiDripstoneRecipe(DripstoneEvent.DIAMOND));
            emiRegistry.addRecipe(new EmiDripstoneRecipe(DripstoneEvent.AMETHYST));
        }
    }

}
