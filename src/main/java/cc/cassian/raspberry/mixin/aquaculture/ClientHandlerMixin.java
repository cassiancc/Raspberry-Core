package cc.cassian.raspberry.mixin.aquaculture;

import cc.cassian.raspberry.PlayerWithGrapplingHook;
import com.teammetallurgy.aquaculture.client.ClientHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientHandler.class)
public class ClientHandlerMixin {

    // Make the fishing rod item properly render without a line
    @Inject(method = "lambda$registerFishingRodModelProperties$2(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/LivingEntity;I)F", at = @At("RETURN"), cancellable = true, remap = false)
    private static void grapplingHookCast(ItemStack stack, ClientLevel level, LivingEntity entity, int i, CallbackInfoReturnable<Float> cir) {
        if (entity != null) {
            boolean isMainhand = entity.getMainHandItem() == stack;
            boolean isOffHand = entity.getOffhandItem() == stack;
            if (entity.getMainHandItem().getItem() instanceof FishingRodItem) {
                isOffHand = false;
            }
            if ((isMainhand || isOffHand) && entity instanceof Player && ((PlayerWithGrapplingHook)entity).raspberryCore$getHook() != null) {
                cir.setReturnValue(1.0F);
            }
        }
    }
}
