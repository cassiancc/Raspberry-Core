package cc.cassian.raspberry.events;

import cc.cassian.raspberry.items.FlowerGarlandItem;
import cc.cassian.raspberry.registry.RaspberryItems;
import cc.cassian.raspberry.registry.RaspberryParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlowerGarlandEvent {
    private static final float STEP_LENGTH = 2.0F; // How far does the player need to move before trying to spawn another particle
    private static final float PARTICLE_CHANCE = 0.25F;
    private static final float PARTICLE_CHANCE_SPRINTING = 0.75F;
    private static final float TWO_PARTICLE_CHANCE = 0.5F;
    private static final Map<UUID, Vec3> prevPositions = new HashMap<>();
    private static final Map<Item, SimpleParticleType> PARTICLES = new HashMap<>();

    static {
        PARTICLES.put(RaspberryItems.CHEERY_WILDFLOWER_GARLAND.get(), RaspberryParticleTypes.CHEERY_PETAL.get());
        PARTICLES.put(RaspberryItems.PLAYFUL_WILDFLOWER_GARLAND.get(), RaspberryParticleTypes.PLAYFUL_PETAL.get());
        PARTICLES.put(RaspberryItems.HOPEFUL_WILDFLOWER_GARLAND.get(), RaspberryParticleTypes.HOPEFUL_PETAL.get());
        PARTICLES.put(RaspberryItems.MOODY_WILDFLOWER_GARLAND.get(), RaspberryParticleTypes.MOODY_PETAL.get());
    }

    public static void tick(Minecraft mc) {
        ParticleStatus status = mc.options.particles().get();
        var player = mc.player;
        if (status == ParticleStatus.MINIMAL) return;
        if (player == null) return;

        Item headItem = player.getItemBySlot(EquipmentSlot.HEAD).getItem();
        if (headItem instanceof FlowerGarlandItem && player.isOnGround()) {
            UUID playerId = player.getUUID();
            Vec3 playerPos = player.position();

            if (!prevPositions.containsKey(playerId)) {
                prevPositions.put(playerId, playerPos);
                return;
            }

            Vec3 prevPlayerPos = prevPositions.get(playerId);
            double distance = playerPos.distanceTo(prevPlayerPos);

            if (distance > STEP_LENGTH && player.getRandom().nextFloat() <= (player.isSprinting() ? PARTICLE_CHANCE_SPRINTING : PARTICLE_CHANCE) ) {
                Vec3 movement = player.getDeltaMovement().normalize().reverse();
                SimpleParticleType particle = PARTICLES.get(headItem);

                if (particle != null) {
                    int particleCount = player.getRandom().nextFloat() <= TWO_PARTICLE_CHANCE ? 1 : 2;
                    for (int i = 0; i < particleCount; i++) {
                        player.level.addParticle(
                            particle,
                            playerPos.x() + (movement.x/2),
                            playerPos.y()+1 + player.getRandom().nextFloat() * 0.4,
                            playerPos.z() + (movement.z/2),
                            0, 0, 0
                        );
                    }
                }
                prevPositions.put(playerId, playerPos);
            }
        }
    }
}
