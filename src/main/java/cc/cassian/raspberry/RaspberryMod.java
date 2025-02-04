package cc.cassian.raspberry;

import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.compat.AquacultureCompat;
import cc.cassian.raspberry.compat.CopperizedCompat;
import cc.cassian.raspberry.compat.NeapolitanCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryItems;
import cc.cassian.raspberry.registry.RasperryMobEffects;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RaspberryMod.MOD_ID)
public final class RaspberryMod {
    public static final String MOD_ID = "raspberry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RaspberryMod(FMLJavaModLoadingContext context) {
        var eventBus = context.getModEventBus();
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        // Register deferred registers.
        RaspberryBlocks.BLOCKS.register(eventBus);
        RaspberryItems.ITEMS.register(eventBus);
        RasperryMobEffects.MOB_EFFECTS.register(eventBus);
        // Register config
        registerModsPage(context);
        // Register event bus listeners.
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltipEvent);
        eventBus.addListener(RaspberryMod::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::copperTick);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::lightningTick);
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("neapolitan"))
            NeapolitanCompat.boostAgility();
    }

    @SubscribeEvent
    public static void lightningTick(EntityStruckByLightningEvent event) {
        var modlist = ModList.get();
        if (modlist.isLoaded("copperized") && !modlist.isLoaded("cofh_core"))
            CopperizedCompat.electrify(event);
    }

    @SubscribeEvent
    public static void copperTick(TickEvent.PlayerTickEvent event) {
        var modlist = ModList.get();
        if (modlist.isLoaded("copperized") && modlist.isLoaded("cofh_core"))
            CopperizedCompat.resist(event);
    }

    /**
	 * Integrate Cloth Config screen (if mod present) with Forge mod menu.
	 */
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModList.get().isLoaded("cloth_config"))
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ModList.get().isLoaded("aquaculture"))
            AquacultureCompat.checkAndAddTooltip(event);
    }
}
