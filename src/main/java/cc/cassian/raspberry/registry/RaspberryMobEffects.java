package cc.cassian.raspberry.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    public static final RegistryObject<MobEffect> AFTERSHOCK = MOB_EFFECTS.register("aftershock",
            () -> new Aftershock( MobEffectCategory.BENEFICIAL, 10076657)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "CE4EFE3F-12D8-4C0A-AA36-312EEE9DBEF3", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "CE4EFE3F-12D8-4C0A-AA36-5BA2BB9FFFF3", 0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );

    private static class Aftershock extends MobEffect {
        public Aftershock(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
