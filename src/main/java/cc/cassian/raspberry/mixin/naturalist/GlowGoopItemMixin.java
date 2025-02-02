package cc.cassian.raspberry.mixin.naturalist;

import cc.cassian.raspberry.config.ModConfig;
import com.starfish_studios.naturalist.item.GlowGoopItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Pseudo
@Mixin(GlowGoopItem.class)
public class GlowGoopItemMixin {

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;hasShiftDown()Z"))
    public boolean appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
        if (ModConfig.get().hideTooltips)
            return false;
        else return Screen.hasShiftDown();
    }

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    public <E> boolean appendHoverText(List<Component> instance, E e) {
        if (ModConfig.get().hideTooltips)
            return false;
        else return instance.add((MutableComponent)e);
    }
}
