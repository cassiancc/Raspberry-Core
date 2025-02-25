package cc.cassian.raspberry.registry;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MOD_ID);

    public static final RegistryObject<Attribute> KINETIC_DAMAGE = register("kinetic_damage", 0.0, 0.0, 30.0);

    private static RegistryObject<Attribute> register(String name, double defaultValue, double min, double max) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute.%s.%s".formatted(MOD_ID, name), defaultValue, min, max));
    }
}
