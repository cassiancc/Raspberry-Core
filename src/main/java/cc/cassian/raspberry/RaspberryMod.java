package cc.cassian.raspberry;

import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.compat.AquacultureCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RaspberryMod.MOD_ID)
public final class RaspberryMod {
    public static final String MOD_ID = "raspberry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RaspberryMod(FMLJavaModLoadingContext context) {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        RaspberryBlocks.BLOCKS.register(context.getModEventBus());
        RaspberryItems.ITEMS.register(context.getModEventBus());
        registerModsPage(context);
        addTooltips();
    }

    /**
	 * Integrate Cloth Config screen (if mod present) with Forge mod menu.
	 */
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModList.get().isLoaded("cloth_config"))
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

    public void addTooltips() {
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltipEvent);
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("aquaculture"))
            AquacultureCompat.checkAndAddTooltip(event);
    }
}
