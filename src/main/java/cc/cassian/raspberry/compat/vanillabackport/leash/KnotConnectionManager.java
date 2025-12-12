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
        List<LeashFenceKnotEntity> knots = this.getConnectedKnots(self);
        boolean snappedAny = false;

        for (LeashFenceKnotEntity knot : knots) {
            double d = self.distanceTo(knot);
            if (d > 10.0 * 2) { 
                snappedAny = true;
                removeConnection(self, knot);
                if (!self.level.isClientSide) {
                    self.spawnAtLocation(Items.LEAD);
                    KnotInteractionHelper.syncKnots(knot);
                }
            }
        }

        if (snappedAny && !self.level.isClientSide) {
            KnotInteractionHelper.syncKnots(self);
        }
    }
    
    public static boolean createConnection(LeashFenceKnotEntity knotA, LeashFenceKnotEntity knotB) {
        if (knotA == knotB) return false;
        
        KnotConnectionManager managerA = getManager(knotA);
        KnotConnectionManager managerB = getManager(knotB);
        
        boolean addedA = managerA.connectedKnotUuids.add(knotB.getUUID());
        boolean addedB = managerB.connectedKnotUuids.add(knotA.getUUID());
        
        return addedA || addedB;
    }
    
    public static boolean removeConnection(LeashFenceKnotEntity knotA, LeashFenceKnotEntity knotB) {
        if (knotA == knotB) return false;
        
        KnotConnectionManager managerA = getManager(knotA);
        KnotConnectionManager managerB = getManager(knotB);
        
        boolean removedA = managerA.connectedKnotUuids.remove(knotB.getUUID());
        boolean removedB = managerB.connectedKnotUuids.remove(knotA.getUUID());
        
        return removedA || removedB;
    }
    
    public List<LeashFenceKnotEntity> getConnectedKnots(LeashFenceKnotEntity self) {
        List<LeashFenceKnotEntity> connectedKnots = new ArrayList<>();
        Iterator<UUID> iterator = connectedKnotUuids.iterator();
        Level level = self.level;

        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            if (level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof LeashFenceKnotEntity knot && !knot.isRemoved()) {
                    connectedKnots.add(knot);
                } else {
                    iterator.remove();
                }
            } else {
                List<LeashFenceKnotEntity> entities = level.getEntitiesOfClass(LeashFenceKnotEntity.class, 
                    new AABB(self.blockPosition()).inflate(50), e -> e.getUUID().equals(uuid));
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
    
    public int getConnectionCount() {
        return connectedKnotUuids.size();
    }
    
    public void clearAllConnections(Level level, LeashFenceKnotEntity self) {
        if (level instanceof ServerLevel serverLevel) {
            for (UUID uuid : new ArrayList<>(connectedKnotUuids)) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof LeashFenceKnotEntity knot) {
                    getManager(knot).connectedKnotUuids.remove(self.getUUID());
                    KnotInteractionHelper.syncKnots(knot);
                }
            }
        }
        connectedKnotUuids.clear();
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
            for (int i = 0; i < list.size(); i++) {
                connectedKnotUuids.add(NbtUtils.loadUUID(list.get(i)));
            }
        }
    }
    
    public static KnotConnectionManager getManager(LeashFenceKnotEntity knot) {
        return ((KnotConnectionAccess) knot).raspberry$getConnectionManager();
    }
}