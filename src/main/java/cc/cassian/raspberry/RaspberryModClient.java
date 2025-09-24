package cc.cassian.raspberry;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import oshi.util.tuples.Pair;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RaspberryModClient {
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        if (ModCompat.SUPPLEMENTARIES && ModCompat.FARMERS_DELIGHT) {
            BlockColors blockColors = event.getBlockColors();
            for (Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block : RaspberryBlocks.FOLIAGE_BLOCKS) {
                event.register(((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {
                        return 9551193;
                    }
                    return BiomeColors.getAverageFoliageColor(view, pos);
                }), block.getA().get());
            }
        }

    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ModCompat.FARMERS_DELIGHT && ModCompat.SUPPLEMENTARIES) {
            event.registerEntityRenderer(RaspberryEntityTypes.ASHBALL.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), ThrownItemRenderer::new);
        }
    }
}
