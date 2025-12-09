package cc.cassian.raspberry.client;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.client.entity.renderer.SwapArrowRenderer;
import cc.cassian.raspberry.events.FlowerGarlandEvent;
import cc.cassian.raspberry.registry.BlockSupplier;
import cc.cassian.raspberry.client.registry.RaspberryItemProperties;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RaspberryModClient {

    public static void init(FMLJavaModLoadingContext context) {
        // Register config
        registerModsPage(context);
        MinecraftForge.EVENT_BUS.addListener(RaspberryModClient::clickTick);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        BlockColors blockColors = event.getBlockColors();
        for (BlockSupplier block : RaspberryBlocks.FOLIAGE_BLOCKS) {
            event.register(((state, view, pos, tintIndex) -> {
                if (view == null || pos == null) {
                    return 9551193;
                }
                return BiomeColors.getAverageFoliageColor(view, pos);
            }), block.getBlock());
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RaspberryEntityTypes.ASHBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.SWAP_ARROW.get(), SwapArrowRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RaspberryBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
        });

        RaspberryItemProperties.register();
    }

    // Forge event bus.
    public static void clickTick(TickEvent.ClientTickEvent event) {
        FlowerGarlandEvent.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.RegisterAdditional event) {
        // This should probably be extracted into a handler of some kind
        event.register(RaspberryMod.locate("block/cheery_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/moody_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/playful_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/hopeful_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/clovers_potted"));
    }


    /**
     * Integrate Cloth Config screen (if mod present) with Forge mod menu.
     */
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModCompat.CLOTH_CONFIG)
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

}
