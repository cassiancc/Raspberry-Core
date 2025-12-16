/* The MIT License (MIT)

Copyright (c) 2025 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */


package cc.cassian.raspberry.compat.vanillabackport.leash;

import java.util.List;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.vanillabackport.leash.network.KnotConnectionSyncPacket;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID)
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
                List<Leashable> nearbyMobs = Leashable.leashableInArea(target.level, target.position(), l -> l.raspberry$getLeashHolder() == player);

                if (!nearbyMobs.isEmpty()) {
                    boolean attachedAny = false;

                    for (Leashable sourceMob : nearbyMobs) {
                        if (sourceMob instanceof LeashFenceKnotEntity && target instanceof LeashFenceKnotEntity) {
                            continue;
                        }

                        if (target instanceof Leashable targetLeashable && targetLeashable.raspberry$getLeashHolder() == (Entity) sourceMob) {
                            continue;
                        }

                        if (sourceMob.canHaveALeashAttachedTo(target)) {
                            sourceMob.raspberry$setLeashedTo(target, true);
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

        if (stack.is(Tags.Items.SHEARS) && shearOffAllLeashConnections(target, player)) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (target.isAlive() && target instanceof Leashable leashable) {
            if (leashable.raspberry$getLeashHolder() == player) {
                if (!target.level.isClientSide) {
                    leashable.raspberry$dropLeash(true, !player.isCreative());
                    target.level.gameEvent(GameEvent.ENTITY_INTERACT, target.position(), GameEvent.Context.of(player));
                    target.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            if (stack.is(Items.LEAD) && !(leashable.raspberry$getLeashHolder() instanceof Player)) {
                if (target instanceof LeashFenceKnotEntity) {
                    return;
                }

                if (!target.level.isClientSide && leashable.canHaveALeashAttachedTo(player)) {
                    if (leashable.raspberry$isLeashed()) {
                        leashable.raspberry$dropLeash(true, true);
                    }

                    leashable.raspberry$setLeashedTo(player, true);
                    target.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);

                    if (!player.isCreative()) stack.shrink(1);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!ModConfig.get().backportLeash) return;

        Entity target = event.getTarget();
        if (target instanceof LeashFenceKnotEntity knot && target instanceof KnotConnectionAccess access) {
            KnotConnectionManager manager = access.raspberry$getConnectionManager();
            
            if (manager.hasConnections()) {
                KnotConnectionSyncPacket packet = new KnotConnectionSyncPacket(knot.getId(), manager.getConnectedUuids());
                packet.sendTo((net.minecraft.server.level.ServerPlayer) event.getEntity()); 
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!ModConfig.get().backportLeash) return;
        
        BlockState state = event.getState();
        if (!state.is(BlockTags.FENCES)) return;
        
        Level level = (Level) event.getLevel();
        if (level.isClientSide) return;
        
        BlockPos pos = event.getPos();
        
        List<LeashFenceKnotEntity> knots = level.getEntitiesOfClass(
            LeashFenceKnotEntity.class,
            new AABB(pos),
            knot -> knot.getPos().equals(pos)
        );
        
        for (LeashFenceKnotEntity knot : knots) {
            if (knot instanceof KnotConnectionAccess) {
                KnotInteractionHelper.discardCustomConnections(knot, (Entity) event.getPlayer());
            }
            knot.discard();
        }
    }

    public static boolean shearOffAllLeashConnections(Entity entity, Player player) {
        boolean sheared = dropAllLeashConnections(entity, player);
        if (sheared && entity.level instanceof ServerLevel server) {
            server.playSound(null, entity.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return sheared;
    }

    public static boolean dropAllLeashConnections(Entity entity, @Nullable Player player) {
        List<Leashable> leashed = Leashable.leashableLeashedTo(entity);
        boolean dropConnections = !leashed.isEmpty();

        if (entity instanceof Leashable leashable && leashable.raspberry$isLeashed()) {
            leashable.raspberry$dropLeash(true, true);
            dropConnections = true;
        }

        for (Leashable leashable : leashed) {
            leashable.raspberry$dropLeash(true, true);
        }
        
        if (entity instanceof LeashFenceKnotEntity knot && entity instanceof KnotConnectionAccess) {
            KnotInteractionHelper.discardCustomConnections(knot, (Entity) player);
            dropConnections = true;
        }

        if (dropConnections) {
            entity.gameEvent(GameEvent.SHEAR, player);
            if (entity instanceof LeashFenceKnotEntity) {
                entity.discard();
            }

            return true;
        } else {
            return false;
        }
    }
}