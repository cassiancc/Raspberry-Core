package cc.cassian.raspberry.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;
import static cc.cassian.raspberry.RaspberryMod.locate;

public class RaspberrySoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> ASHBALL_THROW = register("entity.ashball.throw");
    public static final RegistryObject<SoundEvent> CRUNCHY_FRUIT_SOUNDS = register("entity.crunchy_fruit.eat");
    public static final RegistryObject<SoundEvent> SOFT_FRUIT_SOUNDS = register("entity.soft_fruit.eat");
    public static final RegistryObject<SoundEvent> DRIED_KELP_SOUNDS = register("entity.dried_kelp.eat");
    public static final RegistryObject<SoundEvent> STEW_SOUNDS = register("entity.stew.eat");
    public static final RegistryObject<SoundEvent> VEGETABLE_SOUNDS = register("entity.vegetable.eat");

    public static final RegistryObject<SoundEvent> SILVER_HIT = register("entity.player.attack.silver_hit");

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, ()-> SoundEvent.createVariableRangeEvent(locate(key)));
    }
}
