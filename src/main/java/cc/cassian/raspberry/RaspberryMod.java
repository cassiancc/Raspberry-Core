package cc.cassian.raspberry;

import cc.cassian.raspberry.client.RaspberryModClient;
import cc.cassian.raspberry.compat.*;
import cc.cassian.raspberry.compat.oreganized.OreganizedEvents;
import cc.cassian.raspberry.compat.oreganized.network.RaspberryOreganizedNetwork;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.effect.InfestedMobEffect;
import cc.cassian.raspberry.entity.SwapArrowEntity;
import cc.cassian.raspberry.events.AftershockEvent;
import cc.cassian.raspberry.events.ChangeWeatherEvent;
import cc.cassian.raspberry.events.DarknessRepairEvent;
import cc.cassian.raspberry.network.RaspberryNetwork;
import cc.cassian.raspberry.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cc.cassian.raspberry.registry.RaspberryBlocks.FOLIAGE_BLOCKS;

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
        // Register network.
        RaspberryNetwork.register();
        // Register deferred registers.
        RaspberryBlocks.register(eventBus);
        RaspberryFluids.FLUIDS.register(eventBus);
        RaspberryFluids.FLUID_TYPES.register(eventBus);
        RaspberryItems.ITEMS.register(eventBus);
        RaspberryMobEffects.MOB_EFFECTS.register(eventBus);
        RaspberryEntityTypes.ENTITIES.register(eventBus);
        RaspberrySoundEvents.SOUNDS.register(eventBus);
        RaspberryParticleTypes.PARTICLE_TYPES.register(eventBus);
        // Register event bus listeners.
        if (ModCompat.AQUACULTURE)
            MinecraftForge.EVENT_BUS.addListener(AquacultureCompat::checkAndAddTooltip);
        if (ModCompat.ENVIRONMENTAL) {
			MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onEntityInteract);
            MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onEntityJoinWorld);
            MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onLivingUpdate);
		}
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(ChangeWeatherEvent::tick);
        eventBus.addListener(RaspberryMod::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(DarknessRepairEvent::playerTick);
        if (!ModCompat.COFH_CORE)
            MinecraftForge.EVENT_BUS.addListener(AftershockEvent::electrify);
        if (ModCompat.OREGANIZED) {
            RaspberryAttributes.ATTRIBUTES.register(eventBus);
            RaspberryOreganizedNetwork.register();
            MinecraftForge.EVENT_BUS.addListener(OreganizedEvents::onItemAttributes);
            MinecraftForge.EVENT_BUS.addListener(OreganizedEvents::onHurtEvent);
        }
        if (ModCompat.MINERS_DELIGHT) {
            MinecraftForge.EVENT_BUS.addListener(MinersDelightCompat::infestedInteract);
        }
        MinecraftForge.EVENT_BUS.addListener(InfestedMobEffect::onMobHurt);
        if (FMLEnvironment.dist.isClient()) {
            RaspberryModClient.init(context);
        }
        if (ModCompat.BLUEPRINT) {
            RaspberryData.register();
        }
    }

    public static ResourceLocation locate(String id) {
        return identifier(MOD_ID, id);
    }

    public static ResourceLocation identifier(String namespace, String id) {
        return new ResourceLocation(namespace, id);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModCompat.NEAPOLITAN)
            NeapolitanCompat.boostAgility();
        if (ModCompat.QUARK) {
            QuarkCompat.register();
        }
        for (BlockSupplier foliageBlock : FOLIAGE_BLOCKS) {
            ComposterBlock.COMPOSTABLES.put(foliageBlock.getBlockSupplier().get(), 0.3f);
        }
        if (ModCompat.SUPPLEMENTARIES) {
            SupplementariesCompat.register();
        }

        event.enqueueWork(RaspberryBlocks::addPottedPlants);
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getDirectEntity() instanceof SwapArrowEntity) {
            // Copies the way Caverns and Chasms make Blunt Arrows deal no damage
            event.setAmount(0.0F);
        }
    }
}
