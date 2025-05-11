package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.Items;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static EmiRecipeCategory ANVIL = new EmiRecipeCategory(RaspberryMod.locate("anvil"), EmiStack.of(Items.ANVIL));
    public static EmiRecipeCategory BEACON_BASE = new EmiRecipeCategory(RaspberryMod.locate("beacon_base"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory BEACON_PAYMENT = new EmiRecipeCategory(RaspberryMod.locate("beacon_payment"), EmiStack.of(Items.BEACON));


    @Override
    public void register(EmiRegistry emiRegistry) {
        if (ModCompat.CREATE && ModCompat.DOMESTICATION_INNOVATION && ModCompat.ENSORCELLATION && ModCompat.SUPPLEMENTARIES && ModCompat.ALLUREMENT) {
            EmiSmithingRecipe.addEnchantments(emiRegistry);
        }
        if (ModCompat.QUARK) {
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.CHIPPED_ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.DAMAGED_ANVIL));
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
    }

}
