package cc.cassian.raspberry.mixin.naturalist;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryItems;
import com.starfish_studios.naturalist.entity.Caterpillar;
import com.starfish_studios.naturalist.entity.Firefly;
import com.starfish_studios.naturalist.helper.ItemHelper;
import com.starfish_studios.naturalist.registry.NaturalistRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Caterpillar.class)
public class CaterpillarMixin {

    @Inject(method = "saveToHandTag", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void stackableButterflies(ItemStack stack, CallbackInfo ci) {
        if (ModConfig.get().naturalist_stackableItems)
            ci.cancel();
    }

}
