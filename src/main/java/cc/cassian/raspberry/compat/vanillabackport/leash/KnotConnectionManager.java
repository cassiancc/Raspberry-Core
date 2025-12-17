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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class KnotConnectionManager {
    private static final String CONNECTIONS_NBT_KEY = "KnotConnections";
    private final Set<UUID> connectedKnotUuids = new HashSet<>();

    public void checkDistance(LeashFenceKnotEntity self) {
        if (self.level.isClientSide) return;

        List<LeashFenceKnotEntity> knots = this.getConnectedKnots(self);
        boolean snappedAny = false;

        for (LeashFenceKnotEntity knot : knots) {
            double d = self.distanceTo(knot);
            if (d > 12.0) {
                snappedAny = true;
                removeConnection(self, knot);
                self.spawnAtLocation(Items.LEAD);
                KnotInteractionHelper.syncKnots(knot);
            }
        }

        if (snappedAny) {
            KnotInteractionHelper.syncKnots(self);
        }
    }

    public static boolean createConnection(LeashFenceKnotEntity knotA, LeashFenceKnotEntity knotB) {
        if (knotA == knotB) return false;

        KnotConnectionManager managerA = getManager(knotA);
        KnotConnectionManager managerB = getManager(knotB);

        boolean addedA = managerA.connectedKnotUuids.add(knotB.getUUID());
        boolean addedB = managerB.connectedKnotUuids.add(knotA.getUUID());

        if (addedA || addedB) {
            KnotInteractionHelper.syncKnots(knotA);
            KnotInteractionHelper.syncKnots(knotB);
        }

        return addedA || addedB;
    }

    public static void removeConnection(LeashFenceKnotEntity knotA, LeashFenceKnotEntity knotB) {
        if (knotA == knotB) return;

        KnotConnectionManager managerA = getManager(knotA);
        KnotConnectionManager managerB = getManager(knotB);

        managerA.connectedKnotUuids.remove(knotB.getUUID());
        managerB.connectedKnotUuids.remove(knotA.getUUID());

        KnotInteractionHelper.syncKnots(knotA);
        KnotInteractionHelper.syncKnots(knotB);
    }

    public List<LeashFenceKnotEntity> getConnectedKnots(LeashFenceKnotEntity self) {
        List<LeashFenceKnotEntity> connectedKnots = new ArrayList<>();
        Level level = self.level;

        if (level instanceof ServerLevel serverLevel) {
            Iterator<UUID> iterator = connectedKnotUuids.iterator();
            while (iterator.hasNext()) {
                UUID uuid = iterator.next();
                Entity entity = serverLevel.getEntity(uuid);

                if (entity instanceof LeashFenceKnotEntity knot) {
                    if (!knot.isRemoved()) {
                        connectedKnots.add(knot);
                    } else {
                        iterator.remove();
                    }
                }
            }
        } else {
            for (UUID uuid : connectedKnotUuids) {
                List<LeashFenceKnotEntity> entities = level.getEntitiesOfClass(
                        LeashFenceKnotEntity.class,
                        new AABB(self.blockPosition()).inflate(50),
                        e -> e.getUUID().equals(uuid)
                );

                if (!entities.isEmpty()) {
                    connectedKnots.add(entities.get(0));
                }
            }
        }

        return connectedKnots;
    }

    public Set<UUID> getConnectedUuids() {
        return new HashSet<>(connectedKnotUuids);
    }

    public void setConnectedUuids(Set<UUID> uuids) {
        this.connectedKnotUuids.clear();
        this.connectedKnotUuids.addAll(uuids);
    }

    public boolean hasConnections() {
        return !connectedKnotUuids.isEmpty();
    }

    public void clearAllConnections(Level level, LeashFenceKnotEntity self) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (UUID uuid : new ArrayList<>(connectedKnotUuids)) {
            Entity entity = serverLevel.getEntity(uuid);

            self.spawnAtLocation(Items.LEAD);

            if (entity instanceof LeashFenceKnotEntity knot) {
                getManager(knot).connectedKnotUuids.remove(self.getUUID());

                KnotInteractionHelper.syncKnots(knot);

                boolean hasVanilla = !Leashable.leashableLeashedTo(knot).isEmpty();
                boolean isBeingLeashed = knot instanceof Leashable leashable && leashable.raspberry$getLeashHolder() != null;
                boolean hasCustom = getManager(knot).hasConnections();

                if (!hasVanilla && !isBeingLeashed && !hasCustom) {
                    knot.discard();
                }
            }
        }
        connectedKnotUuids.clear();
        KnotInteractionHelper.syncKnots(self);
    }

    public void writeToNbt(CompoundTag tag) {
        if (!connectedKnotUuids.isEmpty()) {
            ListTag list = new ListTag();
            for (UUID uuid : connectedKnotUuids) {
                list.add(NbtUtils.createUUID(uuid));
            }
            tag.put(CONNECTIONS_NBT_KEY, list);
        }
    }

    public void readFromNbt(CompoundTag tag) {
        connectedKnotUuids.clear();
        if (tag.contains(CONNECTIONS_NBT_KEY)) {
            ListTag list = tag.getList(CONNECTIONS_NBT_KEY, Tag.TAG_INT_ARRAY);
            for (Tag value : list) {
                connectedKnotUuids.add(NbtUtils.loadUUID(value));
            }
        }
    }

    public static KnotConnectionManager getManager(LeashFenceKnotEntity knot) {
        return ((KnotConnectionAccess) knot).raspberry$getConnectionManager();
    }
}
