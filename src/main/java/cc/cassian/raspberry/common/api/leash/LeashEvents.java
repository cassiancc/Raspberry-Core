package cc.cassian.raspberry.common.api.leash;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "raspberry")
public class LeashEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!ModConfig.get().backportLeash) {
            return;
        }

        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();
        
        if (!target.level.isClientSide && player.isSecondaryUseActive() && target instanceof Leashable leashable && leashable.canBeLeashed(player) && target.isAlive()) {
            if (!(target instanceof LivingEntity living && living.isBaby())) {
                List<Leashable> nearbyMobs = Leashable.leashableInArea(target.level, target.position(), l -> l.getLeashHolder() == player);

                if (!nearbyMobs.isEmpty()) {
                    boolean attachedAny = false;

                    for (Leashable sourceMob : nearbyMobs) {
                        if (sourceMob.canHaveALeashAttachedTo(target)) {
                            sourceMob.setLeashedTo(target, true);
                            attachedAny = true;
                        }
                    }

                    if (attachedAny) {
                        target.level.gameEvent(GameEvent.ENTITY_INTERACT, target.blockPosition(), GameEvent.Context.of(player));
                        target.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
                        
                        event.setCancellationResult(InteractionResult.SUCCESS);
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }

        if (stack.is(Items.SHEARS) && shearOffAllLeashConnections(target, player)) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (target.isAlive() && target instanceof Leashable leashable) {
            if (leashable.getLeashHolder() == player) {
                if (!target.level.isClientSide) {
                    leashable.dropLeash(true, !player.isCreative());
                    target.level.gameEvent(GameEvent.ENTITY_INTERACT, target.position(), GameEvent.Context.of(player));
                    target.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            if (stack.is(Items.LEAD) && !(leashable.getLeashHolder() instanceof Player)) {
                if (!target.level.isClientSide && leashable.canHaveALeashAttachedTo(player)) {
                    if (leashable.isLeashed()) {
                        leashable.dropLeash(true, true);
                    }

                    leashable.setLeashedTo(player, true);
                    target.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);

                    if (!player.isCreative()) stack.shrink(1);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }
    }

    public static boolean shearOffAllLeashConnections(Entity entity, Player player) {
        boolean sheared = dropAllLeashConnections(entity, player);
        if (sheared && entity.level instanceof ServerLevel server) {
            server.playSound(null, entity.blockPosition(), SoundEvents.SHEEP_SHEAR, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return sheared;
    }

    public static boolean dropAllLeashConnections(Entity entity, @Nullable Player player) {
        List<Leashable> leashed = Leashable.leashableLeashedTo(entity);
        boolean dropConnections = !leashed.isEmpty();

        if (entity instanceof Leashable leashable && leashable.isLeashed()) {
            leashable.dropLeash(true, true);
            dropConnections = true;
        }

        for (Leashable leashable : leashed) {
            leashable.dropLeash(true, true);
        }

        if (dropConnections) {
            entity.gameEvent(GameEvent.SHEAR, player);
            return true;
        } else {
            return false;
        }
    }
}