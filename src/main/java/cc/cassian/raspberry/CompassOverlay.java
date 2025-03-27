package cc.cassian.raspberry;

import cc.cassian.raspberry.compat.CavernsAndChasmsCompat;
import cc.cassian.raspberry.compat.MapAtlasesCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class CompassOverlay {
    public static boolean hasCompass = false;
    public static boolean hasDepthGauge = false;


    @SubscribeEvent
    public static void pickup(PlayerEvent.ItemPickupEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void join(PlayerEvent.PlayerLoggedInEvent event) {
        checkInventoryForItems(event.getEntity());
    }

    @SubscribeEvent
    public static void toss(ItemTossEvent event) {
        checkInventoryForItems(event.getPlayer());
    }

    @SubscribeEvent
    public static void closeInventory(PlayerContainerEvent.Close event) {
        checkInventoryForItems(event.getEntity());
    }

    public static void checkInventoryForItems(Player player) {
        var xz = Items.COMPASS;
        var y = Items.COMPASS;
        if (ModCompat.SPELUNKERY)
            y = SpelunkeryCompat.getDepthGauge();
        if (ModConfig.get().overlay_enable) {
            var inventory = player.getInventory();
            if (ModConfig.get().overlay_requireItemInHand) {
                var main = player.getMainHandItem();
                var offhand = player.getOffhandItem();
                hasCompass = main.is(xz) || offhand.is(xz);
                hasDepthGauge = main.is(y) || offhand.is(y);
                if (!hasDepthGauge && ModCompat.CAVERNS_AND_CHASMS) {
                    y = CavernsAndChasmsCompat.getDepthGauge();
                    hasDepthGauge = main.is(y) || offhand.is(y);
                }

            }
            else {
                hasCompass = checkInventoryForItem(inventory, xz, "minecraft:compass");
                hasDepthGauge = checkInventoryForItem(inventory, y, "spelunkery:depth_gauge");
                if (!hasDepthGauge && ModCompat.CAVERNS_AND_CHASMS) {
                    y = CavernsAndChasmsCompat.getDepthGauge();
                    hasDepthGauge = checkInventoryForItem(inventory, y, "caverns_and_chasms:depth_gauge");
                }
            }
        }
        else {
            hasCompass = false;
            hasDepthGauge = false;
        }
    }

    public static boolean checkInventoryForItem(Inventory inventory, Item item, String name) {
        if (inventory.contains(item.getDefaultInstance())) {
            return true;
        }
        if (ModConfig.get().overlay_searchContainers && hasContainer(inventory)) {
            for (ItemStack stack : inventory.items) {
                if (stack.getTag() != null) {
                    var items = stack.getTag().get("Items");
                    if (items != null) {
                        if (items.toString().contains(name)) {
                            return true;
                        }
                    }
                    var be = stack.getTag().get("BlockEntityTag");
                    if (be != null) {
                        if (be.toString().contains(name)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean hasContainer(Inventory inventory) {
        if (inventory.contains(Items.BUNDLE.getDefaultInstance()))
            return true;
        else if (inventory.contains(ModRegistry.SACK.get().asItem().getDefaultInstance()))
            return true;
        else if (inventory.contains(ModRegistry.SAFE.get().asItem().getDefaultInstance()))
            return true;
        else if (inventory.contains(Items.SHULKER_BOX.getDefaultInstance()))
            return true;
        return false;
    }


    @SubscribeEvent
    public static void renderGameOverlayEvent(CustomizeGuiOverlayEvent.DebugText event) {
        if (!hasCompass && !hasDepthGauge)
            return;
        if (ModCompat.MAP_ATLASES && MapAtlasesCompat.showingCoords())
            return;
        var mc = Minecraft.getInstance();
        if (mc.options.renderDebug && !mc.options.reducedDebugInfo().get())
            return;

        ArrayList<String> coords = new ArrayList<>();

        BlockPos pos;
        if (mc.player != null) pos = mc.player.blockPosition();
        else return;

        String x = String.format("%d", pos.getX());
        String y = String.format("%d", pos.getY());
        String z = String.format("%d", pos.getZ());
        var width = Integer.max(x.length(), z.length());
        width = Integer.max(width, y.length());
        x = StringUtils.leftPad(x, width);
        y = StringUtils.leftPad(y, width);
        z = StringUtils.leftPad(z, width);
        int offset = 3;
        int top = ModConfig.get().overlay_position_vertical;
        int textureSize = 256;
        int fontWidth = mc.font.width(StringUtils.repeat("a", width+2));

        if (hasCompass) {
            coords.add("§%sX:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_x_colour), x));
            if (hasDepthGauge) {
                coords.add("§%sY:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_y_colour), y));
            }
            coords.add("§%sZ:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_z_colour), z));
        }
        else if (hasDepthGauge) {
            coords.add("§%sY:§f %s".formatted(ModHelpers.getColour(ModConfig.get().overlay_y_colour), y));
        }

        int textureOffset = 9;  // only depth gauge
        int tooltipSize = 14;  // only depth gauge
        if (hasCompass & hasDepthGauge) { // depth gauge and compass
            textureOffset = 51;
            tooltipSize = 33;
        }
        else if (hasCompass) { // only compass
            textureOffset = 27;
        }

        int windowWidth = mc.getWindow().getGuiScaledWidth();
        int placement = windowWidth-2-fontWidth;
        var poseStack = event.getPoseStack();
        RenderSystem.setShaderTexture(0, RaspberryMod.locate("textures/gui/tooltip.png"));
        GuiComponent.blit(poseStack,
                placement-offset-4, top-3,
                0, 0,
                textureOffset, fontWidth+offset+4, tooltipSize,
                textureSize, textureSize);
        GuiComponent.blit(poseStack,
                windowWidth-4, top-3,
                0, 197,
                textureOffset, offset, tooltipSize,
                textureSize, textureSize);
        for (String text : coords) {
            GuiComponent.drawString(poseStack, mc.font, text, placement-offset, top, 14737632);
            top += mc.font.lineHeight;
        }
    }
}
