package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {
    @Inject(method = "getIconItem", at = @At("TAIL"), cancellable = true)
    private void getIconItem(CallbackInfoReturnable<ItemStack> info) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;

        ResourceLocation tabKey = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);

        if (tabKey != null) {
            String tabName = tabKey.toString();
            Map<String, String> overrides = ModConfig.get().creativeTabIcons;

            if (overrides.containsKey(tabName)) {
                String newItemId = overrides.get(tabName);
                if (newItemId != null) {
                    ItemStack newIcon = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(newItemId))).getDefaultInstance();
                    info.setReturnValue(newIcon);
                }
            }
        }
    }
}