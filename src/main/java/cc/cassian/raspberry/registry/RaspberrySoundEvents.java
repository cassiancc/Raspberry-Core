package cc.cassian.raspberry.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;
import static cc.cassian.raspberry.RaspberryMod.locate;

public class RaspberrySoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);


    public static final RegistryObject<SoundEvent> ASHBALL_THROW = register("entity.ashball.throw");

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, ()-> new SoundEvent(locate(key)));
    }
}
