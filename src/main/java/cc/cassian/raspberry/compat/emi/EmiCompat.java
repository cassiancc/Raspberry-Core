package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.ItemObliteratorCompat;
import cc.cassian.raspberry.compat.SidekickCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.networking.SetStackPacket;
import cc.cassian.raspberry.networking.RaspberryNetworking;
import cc.cassian.raspberry.events.DripstoneEvent;
import cc.cassian.raspberry.items.MarshmallowOnAStickItem;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.Collections;
import java.util.List;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    public static EmiRecipeCategory ANVIL = new EmiRecipeCategory(RaspberryMod.locate("anvil"), EmiStack.of(Items.ANVIL));
    public static EmiRecipeCategory BEACON_BASE = new EmiRecipeCategory(RaspberryMod.locate("beacon_base"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory BEACON_PAYMENT = new EmiRecipeCategory(RaspberryMod.locate("beacon_payment"), EmiStack.of(Items.BEACON));
    public static EmiRecipeCategory DRIPPING = new EmiRecipeCategory(RaspberryMod.locate("dripping"), EmiStack.of(Items.POINTED_DRIPSTONE));


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

        addMarshmallowOnAStickRecipe(emiRegistry, RaspberryItems.MARSHMALLOW_ON_A_STICK.get(), RaspberryItems.CARAMELIZED_MARSHMALLOW_ON_A_STICK.get());
        addMarshmallowOnAStickRecipe(emiRegistry, RaspberryItems.CARAMELIZED_MARSHMALLOW_ON_A_STICK.get(), RaspberryItems.CHARRED_MARSHMALLOW_ON_A_STICK.get());
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

    private static void addMarshmallowOnAStickRecipe(EmiRegistry emiRegistry, Item input, Item output) {
        int cookingTime = MarshmallowOnAStickItem.COOKING_TIME;
        CampfireCookingRecipe fakeRecipe = new CampfireCookingRecipe(RaspberryMod.locate("/" + output.builtInRegistryHolder().key().location().getPath()), "", Ingredient.of(input), output.getDefaultInstance(), 0, cookingTime);
        EmiCookingRecipe emiRecipe = new EmiMarshmallowCookingRecipe(fakeRecipe, cookingTime, input, output);
        emiRegistry.addRecipe(emiRecipe);
    }
}
