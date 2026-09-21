package cc.cassian.raspberry.client;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.client.config.ModConfigFactory;
import cc.cassian.raspberry.client.entity.renderer.GrapplingHookRenderer;
import cc.cassian.raspberry.client.entity.renderer.SwapArrowRenderer;
import cc.cassian.raspberry.client.model.NetheriteSkilletModel;
import cc.cassian.raspberry.client.music.MusicHandler;
import cc.cassian.raspberry.events.FlowerGarlandEvent;
import cc.cassian.raspberry.events.WikiTooltipEvent;
import cc.cassian.raspberry.registry.BlockSupplier;
import cc.cassian.raspberry.client.registry.RaspberryItemProperties;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RaspberryModClient {

    public static void init(FMLJavaModLoadingContext context) {
        // Register config
        registerModsPage(context);
        MinecraftForge.EVENT_BUS.addListener(FlowerGarlandEvent::tick);
        MinecraftForge.EVENT_BUS.addListener(WikiTooltipEvent::wikiTooltip);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new MusicHandler());
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.BakingCompleted event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelResourceLocation skilletLocation = new ModelResourceLocation(RaspberryMod.locate("netherite_skillet"), "inventory");
        BakedModel skilletModel = modelRegistry.get(skilletLocation);
        ModelResourceLocation skilletCookingLocation = new ModelResourceLocation(RaspberryMod.locate( "netherite_skillet_cooking"), "inventory");
        BakedModel skilletCookingModel = modelRegistry.get(skilletCookingLocation);
        modelRegistry.put(skilletLocation, new NetheriteSkilletModel(event.getModelBakery(), skilletModel, skilletCookingModel));
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        for (BlockSupplier block : RaspberryBlocks.FOLIAGE_BLOCKS) {
            event.register(((state, view, pos, tintIndex) -> {
                if (view == null || pos == null) {
                    return 9551193;
                }
                return BiomeColors.getAverageFoliageColor(view, pos);
            }), block.getBlock());
        }
        event.register(((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) {
                return GrassColor.get(0.5F, 1.0F);
            }
            return BiomeColors.getAverageGrassColor(view, pos);
        }), RaspberryBlocks.SHRUB.getBlock(), RaspberryBlocks.POTTED_SHRUB.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Item event){
        event.register(((stack, view) -> GrassColor.get(0.5F, 1.0F)), RaspberryBlocks.SHRUB.getBlock());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RaspberryEntityTypes.ASHBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.SWAP_ARROW.get(), SwapArrowRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RaspberryBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());

            Minecraft.getInstance()
                .getSoundManager()
                .addListener(new MusicEventListener());
        });

        RaspberryItemProperties.register();
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.RegisterAdditional event) {
        // This should probably be extracted into a handler of some kind
        event.register(RaspberryMod.locate("block/cheery_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/moody_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/playful_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/hopeful_wildflowers_potted"));
        event.register(RaspberryMod.locate("block/clovers_potted"));
        event.register(new ModelResourceLocation(RaspberryMod.locate( "netherite_skillet_cooking"), "inventory"));
    }


    /**
     * Integrate Cloth Config screen (if mod present) with Forge mod menu.
     */
    public static void registerModsPage(FMLJavaModLoadingContext context) {
        if (ModCompat.hasClothConfig())
            context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigFactory::createScreen));
    }

}
