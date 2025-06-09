package cc.cassian.raspberry;

import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.compat.*;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;

import static cc.cassian.raspberry.registry.RaspberryBlocks.FOLIAGE_BLOCKS;

@Mod(RaspberryMod.MOD_ID)
public final class RaspberryMod {
    public static final String MOD_ID = "raspberry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RaspberryMod() {
        var context = ModLoadingContext.get();
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        // Register deferred registers.
        if (ModCompat.FARMERS_DELIGHT && ModCompat.SUPPLEMENTARIES) {
            RaspberryBlocks.register(eventBus);
            RaspberryItems.ITEMS.register(eventBus);
            RaspberryMobEffects.MOB_EFFECTS.register(eventBus);
            RaspberryEntityTypes.ENTITIES.register(eventBus);
            RaspberrySoundEvents.SOUNDS.register(eventBus);
            eventBus.addListener(RaspberryCreativePlacements::set);
        }
        RaspberryParticleTypes.PARTICLE_TYPES.register(eventBus);
        // Register event bus listeners.
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltipEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoinLevel);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
        eventBus.addListener(RaspberryMod::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::playerTick);
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::lightningTick);
        if (FMLEnvironment.dist.isClient()) {
            // Register config
            registerModsPage(context);
        }
        if (ModCompat.BLUEPRINT)
            RaspberryData.registerData();
    }

    public static ResourceLocation locate(String id) {
        return identifier(MOD_ID, id);
    }

    public static ResourceLocation identifier(String namespace, String id) {
        return new ResourceLocation(namespace, id);
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModCompat.NEAPOLITAN)
            NeapolitanCompat.boostAgility();
        if (ModCompat.SUPPLEMENTARIES && ModCompat.FARMERS_DELIGHT) {
            for (Pair<RegistryObject<Block>, RegistryObject<BlockItem>> foliageBlock : FOLIAGE_BLOCKS) {
                ComposterBlock.COMPOSTABLES.put(foliageBlock.getB().get(), 0.3f);
            }
        }

    }

    @SubscribeEvent
    public static void lightningTick(EntityStruckByLightningEvent event) {
        if (ModCompat.COPPERIZED && !ModCompat.COFH_CORE && ModConfig.get().aftershock)
            CopperizedCompat.electrify(event);
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (ModCompat.COPPERIZED && ModCompat.COFH_CORE)
            CopperizedCompat.resist(event);
    }

    /**
	 * Integrate Cloth Config screen (if mod present) with Forge mod menu.
	 */
    public static void registerModsPage(ModLoadingContext context) {
        if (ModCompat.CLOTH_CONFIG)
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

    @SubscribeEvent
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ModCompat.AQUACULTURE)
            AquacultureCompat.checkAndAddTooltip(event);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onEntityInteract(event);
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onEntityJoinWorld(event);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (ModCompat.ENVIRONMENTAL)
            EnvironmentalCompat.onLivingUpdate(event);
    }
}
