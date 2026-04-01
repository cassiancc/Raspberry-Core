package cc.cassian.raspberry;

import cc.cassian.raspberry.client.RaspberryModClient;
import cc.cassian.raspberry.compat.*;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.effect.InfestedMobEffect;
import cc.cassian.raspberry.entity.SwapArrowEntity;
import cc.cassian.raspberry.events.AftershockEvent;
import cc.cassian.raspberry.events.ChangeWeatherEvent;
import cc.cassian.raspberry.events.DarknessRepairEvent;
import cc.cassian.raspberry.networking.RaspberryNetworking;
import cc.cassian.raspberry.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
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

    public RaspberryMod() {
        var context = FMLJavaModLoadingContext.get();
        var eventBus = context.getModEventBus();
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like registries and resources) may still be uninitialized.
        // Proceed with mild caution.
        ModConfig.load();
        // Register deferred registers.
        RaspberryBlocks.register(eventBus);
        RaspberryFluids.FLUIDS.register(eventBus);
        RaspberryFluids.FLUID_TYPES.register(eventBus);
        RaspberryItems.ITEMS.register(eventBus);
        RaspberryMobEffects.MOB_EFFECTS.register(eventBus);
        RaspberryEntityTypes.ENTITIES.register(eventBus);
        RaspberrySoundEvents.SOUNDS.register(eventBus);
        RaspberryParticleTypes.PARTICLE_TYPES.register(eventBus);
        RaspberryNetworking.register();

        // Event Listeners
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::onBlockBreak);
        eventBus.addListener(RaspberryMod::commonSetup);

        // Register event bus listeners.
        if (ModCompat.hasAquaculture())
            MinecraftForge.EVENT_BUS.addListener(AquacultureCompat::checkAndAddTooltip);
        if (ModCompat.hasEnvironmental()) {
			MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onEntityInteract);
            MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onEntityJoinWorld);
            MinecraftForge.EVENT_BUS.addListener(EnvironmentalCompat::onLivingUpdate);
		}
        MinecraftForge.EVENT_BUS.addListener(RaspberryMod::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(ChangeWeatherEvent::tick);
        eventBus.addListener(RaspberryMod::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(DarknessRepairEvent::playerTick);
        if (!ModCompat.hasCofhCore())
            MinecraftForge.EVENT_BUS.addListener(AftershockEvent::electrify);
        if (ModCompat.OREGANIZED) {
            RaspberryAttributes.ATTRIBUTES.register(eventBus);
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
        if (ModCompat.hasBlueprint()) RaspberryData.registerData();
    }

    public static ResourceLocation locate(String id) {
        return identifier(MOD_ID, id);
    }

    public static ResourceLocation identifier(String namespace, String id) {
        return new ResourceLocation(namespace, id);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModCompat.hasNeapolitan()) {
            NeapolitanCompat.boostAgility();
        }
        if (ModCompat.hasQuark()) {
            QuarkCompat.register();
        }
        for (BlockSupplier foliageBlock : FOLIAGE_BLOCKS) {
            ComposterBlock.COMPOSTABLES.put(foliageBlock.getBlockSupplier().get(), 0.3f);
        }
        if (ModCompat.hasSupplementaries()) {
            SupplementariesCompat.register();
        }

        event.enqueueWork(RaspberryBlocks::addPottedPlants);
    }

    @SubscribeEvent
    public static void onBlockBreak(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.onGround() && (player.isFallFlying() || ModConfig.get().fastFlyBlockBreaking)) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        BlockState state = event.getBlock();
        if (state.is(Blocks.WITHER_ROSE)) {
            BlockPos pos = event.getPos();

            if (RaspberryBlocks.WITHER_ROSE_BUSH.getBlock().defaultBlockState().canSurvive(event.getLevel(), pos)
                    && event.getLevel().isEmptyBlock(pos.above())) {

                event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);

                if (!event.getLevel().isClientSide) {
                    if (event.getLevel().random.nextInt(5) == 0) {
                        DoublePlantBlock.placeAt(event.getLevel(), RaspberryBlocks.WITHER_ROSE_BUSH.getBlock().defaultBlockState(), pos, 3);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getDirectEntity() instanceof SwapArrowEntity) {
            // Copies the way Caverns and Chasms make Blunt Arrows deal no damage
            event.setAmount(0.0F);
        }
    }

}