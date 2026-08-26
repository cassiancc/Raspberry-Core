package cc.cassian.raspberry.mixin.emi;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.emi.emi.EmiPort;
import dev.emi.emi.registry.EmiTags;
import elocindev.item_obliterator.forge.ItemObliterator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.stream.Stream;

@Mixin(EmiPort.class)
public class VanillaPluginMixin {
    @ModifyReturnValue(method = "getDisabledItems", at = @At(value = "RETURN"), remap = false)
    private static Stream<Item> mixin(Stream<Item> original) {
        var list = new ArrayList<>(original.toList());
        list.addAll(ItemObliterator.blacklisted_items.stream().map(c-> Registry.ITEM.get(new ResourceLocation(c))).toList());
        return list.stream();
    }
}
