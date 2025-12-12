/* MIT License

Copyright (c) 2025 Martin Kadlec

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package cc.cassian.raspberry.compat.vanillabackport.leash;

import cc.cassian.raspberry.compat.vanillabackport.leash.network.KnotConnectionSyncPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;

public class KnotInteractionHelper {

    public static void syncKnots(LeashFenceKnotEntity knot) {
        if (knot.level.isClientSide) return;
        
        KnotConnectionManager manager = KnotConnectionManager.getManager(knot);
        KnotConnectionSyncPacket packet = new KnotConnectionSyncPacket(knot.getId(), manager.getConnectedUuids());
        
        if (knot.level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (net.minecraft.server.level.ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(
                new net.minecraft.world.level.ChunkPos(knot.blockPosition()), false)) {
                packet.sendTo(player);
            }
        }
    }

    public static class HeldEntities {
        public final List<Leashable> all;
        public final List<Leashable> mobs;
        public final List<LeashFenceKnotEntity> knots;
        public final boolean hasMobs;
        public final boolean hasKnots;

        public HeldEntities(Entity holder) {
            this.all = Leashable.leashableInArea(holder.level, holder.position(), l -> l.getLeashHolder() == holder);
            this.mobs = all.stream().filter(l -> !(l instanceof LeashFenceKnotEntity)).toList();
            this.knots = all.stream()
                    .filter(l -> l instanceof LeashFenceKnotEntity)
                    .map(l -> (LeashFenceKnotEntity) l).toList();
            this.hasMobs = !mobs.isEmpty();
            this.hasKnots = !knots.isEmpty();
        }
        
        public boolean isEmpty() { return all.isEmpty(); }
    }

    public static InteractionResult handleKnotInteraction(Player player, LeashFenceKnotEntity knot) {
        HeldEntities held = new HeldEntities(player);
        boolean isHoldingThisKnot = held.all.stream().anyMatch(l -> l == knot);

        if (held.isEmpty()) {
            HeldEntities heldByKnot = new HeldEntities(knot);
            
            if (heldByKnot.hasMobs && !player.isShiftKeyDown()) {
                for (Leashable mob : heldByKnot.mobs) {
                    if (mob.canBeLeashed(player)) mob.setLeashedTo(player, true);
                }
                knot.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
                if (shouldRemoveKnot(knot)) knot.discard();
                return InteractionResult.CONSUME;
                
            } else if (hasLeadItem(player)) {
                ((Leashable)knot).setLeashedTo(player, true);
                knot.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
                
            } else if (KnotConnectionManager.getManager(knot).hasConnections()) {
                if (player.isShiftKeyDown()) {
                    discardCustomConnections(knot, player);
                } else {
                    pickupCustomConnections(knot, player);
                }
                knot.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            
        } else if (isHoldingThisKnot) {
            ((Leashable)knot).dropLeash(true, true);
            return InteractionResult.SUCCESS;
            
        } else {
            boolean created = false;
            
            for (LeashFenceKnotEntity heldKnot : held.knots) {
                if (heldKnot.distanceTo(knot) > 12.0) continue;

                if (KnotConnectionManager.createConnection(heldKnot, knot)) {
                    created = true;
                    ((Leashable)heldKnot).dropLeash(true, false); 
                    syncKnots(heldKnot);
                }
            }
            
            for (Leashable mob : held.mobs) {
                mob.setLeashedTo(knot, true);
                created = true;
            }

            if (created) {
                syncKnots(knot);
                knot.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0f, 1.0f);
            }
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }

    public static boolean hasLeadItem(Player player) {
        return player.getMainHandItem().getItem() instanceof LeadItem || 
               player.getOffhandItem().getItem() instanceof LeadItem;
    }

    public static void consumeLead(Player player) {
        if (!player.getAbilities().instabuild) {
            if (player.getMainHandItem().getItem() instanceof LeadItem) {
                player.getMainHandItem().shrink(1);
            } else if (player.getOffhandItem().getItem() instanceof LeadItem) {
                player.getOffhandItem().shrink(1);
            }
        }
    }
    
    public static boolean shouldRemoveKnot(LeashFenceKnotEntity knot) {
        boolean hasVanilla = !Leashable.leashableLeashedTo(knot).isEmpty();
        boolean isLeashed = ((Leashable)knot).getLeashHolder() != null;
        boolean hasCustom = KnotConnectionManager.getManager(knot).hasConnections();
        return !hasVanilla && !isLeashed && !hasCustom;
    }

    public static void pickupCustomConnections(LeashFenceKnotEntity knot, Player player) {
        KnotConnectionManager manager = KnotConnectionManager.getManager(knot);
        List<LeashFenceKnotEntity> connected = manager.getConnectedKnots(knot);
        
        for (LeashFenceKnotEntity other : connected) {
            KnotConnectionManager.removeConnection(knot, other);
            
            if (player.distanceToSqr(other) <= 100.0) {
                ((Leashable)other).setLeashedTo(player, true);
                syncKnots(other);
            } else {
                other.spawnAtLocation(Items.LEAD);
                if (shouldRemoveKnot(other)) {
                    other.discard();
                } else {
                    syncKnots(other);
                }
            }
            break; 
        }
        
        if (shouldRemoveKnot(knot)) knot.discard();
        else syncKnots(knot);
    }
    
    public static void discardCustomConnections(LeashFenceKnotEntity knot, Entity breaker) {
        KnotConnectionManager manager = KnotConnectionManager.getManager(knot);
        List<LeashFenceKnotEntity> connected = manager.getConnectedKnots(knot);
        
        for (LeashFenceKnotEntity other : connected) {
            KnotConnectionManager.removeConnection(knot, other);
            if (shouldRemoveKnot(other)) other.discard();
            else syncKnots(other);
            
            knot.spawnAtLocation(Items.LEAD);
        }
        
        if (shouldRemoveKnot(knot)) knot.discard();
        else syncKnots(knot);
        
        knot.gameEvent(GameEvent.BLOCK_DETACH, breaker);
    }
}