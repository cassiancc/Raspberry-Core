package cc.cassian.raspberry;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import oshi.util.tuples.Pair;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RaspberryModClient {
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        BlockColors blockColors = event.getBlockColors();
        for (Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block : RaspberryBlocks.FOLIAGE_BLOCKS) {
            event.register(((state, view, pos, tintIndex) -> {
                assert view != null;
                return BiomeColors.getAverageFoliageColor(view, pos);
            }), block.getA().get());
        }
    }
}
