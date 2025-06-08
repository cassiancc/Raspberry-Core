package cc.cassian.raspberry.registry;

import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class RaspberryParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<SimpleParticleType> MIRROR;

    private static RegistryObject<SimpleParticleType> registerSimpleParticleType(boolean alwaysShow, String name) {
        return PARTICLE_TYPES.register(name, () -> {
            return new SimpleParticleType(alwaysShow);
        });
    }

    public RaspberryParticleTypes() {
    }

    static {
        MIRROR = registerSimpleParticleType(true, "mirror");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
    public static class RegisterParticles {
        public RegisterParticles() {
        }

        @SubscribeEvent
        public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
            event.register((ParticleType)RaspberryParticleTypes.MIRROR.get(), PlayerCloudParticle.Provider::new);
        }
    }

}
