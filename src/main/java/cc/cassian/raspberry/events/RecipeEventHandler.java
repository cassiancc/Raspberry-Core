package cc.cassian.raspberry.events;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.recipe.RecipeModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID)
public class RecipeEventHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (RecipeModifier.errorCount > 0) {
            event.getEntity().sendSystemMessage(
                    Component.literal("Raspberry Core: ")
                            .append(Component.literal(RecipeModifier.errorCount + " recipe errors occurred on startup. check logs.")
                                    .withStyle(ChatFormatting.RED))
            );
        }
    }
}