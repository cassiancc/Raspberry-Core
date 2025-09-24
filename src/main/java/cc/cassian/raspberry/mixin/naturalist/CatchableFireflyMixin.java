package cc.cassian.raspberry.mixin.naturalist;

import cc.cassian.raspberry.registry.RaspberryItems;
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
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public class CatchableFireflyMixin {

    @Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
    private void stackableButterflies(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var entity = (Animal) (Object) this;
        if (entity instanceof Firefly) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (itemStack.getItem().equals(NaturalistRegistry.BUG_NET.get()) && entity.isAlive()) {
                ItemStack caughtItemStack = RaspberryItems.FIREFLY.get().getDefaultInstance();
                itemStack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));

                if (player.getInventory().add(caughtItemStack)) {
                    entity.discard();
                    cir.setReturnValue(InteractionResult.SUCCESS);
                } else {
                    ItemHelper.spawnItemOnEntity(player, caughtItemStack);
                    player.playSound(SoundEvents.ITEM_PICKUP, 0.3F, 1.0F);
                    if (!entity.level.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, caughtItemStack);
                    }

                    entity.discard();
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }

    }

}
