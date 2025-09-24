package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.compat.supplementaries.RoseGoldBombExplosion;
import cc.cassian.raspberry.entity.Ashball;
import cc.cassian.raspberry.entity.RoseGoldBombEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<Ashball>> ASHBALL = register(
            "ashball", EntityType.Builder.<Ashball>of(Ashball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
    );


    public static final RegistryObject<EntityType<RoseGoldBombEntity>> ROSE_GOLD_BOMB = register(
            "rose_gold_bomb", EntityType.Builder.<RoseGoldBombEntity>of(RoseGoldBombEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
    );

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String key, EntityType.Builder<T> builder) {
        return ENTITIES.register(key, ()-> builder.build(key));
    }
}
