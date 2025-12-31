package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.ItemObliteratorCompat;
import cc.cassian.raspberry.compat.SidekickCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.networking.SetStackPacket;
import cc.cassian.raspberry.networking.RaspberryNetworking;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.Items;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static EmiRecipeCategory ANVIL = new EmiRecipeCategory(RaspberryMod.locate("anvil"), EmiStack.of(Items.ANVIL));
    public static EmiRecipeCategory BEACON_BASE = new EmiRecipeCategory(RaspberryMod.locate("beacon_base"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory BEACON_PAYMENT = new EmiRecipeCategory(RaspberryMod.locate("beacon_payment"), EmiStack.of(Items.BEACON));


    @Override
    public void register(EmiRegistry emiRegistry) {
        if (ModConfig.get().emi_tablets && ModCompat.hasCreate() && ModCompat.hasDomesticationInnovation() && ModCompat.hasEnsorcellation() && ModCompat.hasSupplementaries() && ModCompat.hasAllurement()) {
            EmiSmithingRecipe.addEnchantments(emiRegistry);
        }
        if (ModCompat.hasQuark()) {
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.CHIPPED_ANVIL));
            emiRegistry.addWorkstation(EmiCompat.ANVIL, EmiStack.of(Items.DAMAGED_ANVIL));
            emiRegistry.addCategory(ANVIL);
            EmiEtchingRecipe.addRunes(emiRegistry);
        }
        if (ModCompat.hasBetterBeacons() && ModConfig.get().raspberry_beacon_interaction) {
            emiRegistry.addWorkstation(EmiCompat.BEACON_BASE, EmiStack.of(Items.BEACON));
            emiRegistry.addCategory(BEACON_BASE);
            EmiBeaconBaseRecipe.addBeaconRecipe(emiRegistry);
            emiRegistry.addWorkstation(EmiCompat.BEACON_PAYMENT, EmiStack.of(Items.BEACON));
            emiRegistry.addCategory(BEACON_PAYMENT);
            EmiBeaconPaymentRecipe.addBeaconRecipe(emiRegistry);
        }
        if (ModCompat.hasItemObliterator()) {
            emiRegistry.removeEmiStacks(emiStack -> ItemObliteratorCompat.shouldHide(emiStack.getItemStack()));
        }
        emiRegistry.addDragDropHandler(CreativeModeInventoryScreen.class, EmiCompat::handleDragAndDrop);
        emiRegistry.addDragDropHandler(InventoryScreen.class, EmiCompat::handleDragAndDrop);
        if (ModCompat.hasSidekick()) {
            SidekickCompat.addDragAndDrop(emiRegistry);
        }
    }

    public static boolean handleDragAndDrop(AbstractContainerScreen<?> screen, EmiIngredient stack, int x, int y) {
        if (screen.getMinecraft().player != null && screen.getMinecraft().player.hasPermissions(2)) {
            if (screen.getSlotUnderMouse() != null) {
                RaspberryNetworking.sendToServer(new SetStackPacket(screen.getSlotUnderMouse().getContainerSlot(), stack.getEmiStacks().get(0).getItemStack()));
            }
            return true;
        }
        return false;
    }

}
