package cc.cassian.raspberry.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    public static final RegistryObject<MobEffect> AFTERSHOCK = MOB_EFFECTS.register("aftershock",
            () -> new Aftershock( MobEffectCategory.BENEFICIAL, 10076657)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "CE4EFE3F-12D8-4C0A-AA36-312EEE9DBEF3", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "CE4EFE3F-12D8-4C0A-AA36-5BA2BB9FFFF3", 0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );

    public static final RegistryObject<MobEffect> SATISFACTION = MOB_EFFECTS.register("satisfaction",
            () -> new Satisfaction( MobEffectCategory.BENEFICIAL, 10076657)
    );

    public static final RegistryObject<MobEffect> COUGHING = MOB_EFFECTS.register("coughing",
            () -> new Coughing(MobEffectCategory.HARMFUL, 10076657)
    );

    public static final RegistryObject<MobEffect> PANIC = MOB_EFFECTS.register("panic",
            () -> new Panic(MobEffectCategory.BENEFICIAL, 15494786)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "CE4EFE3F-12D8-4C0A-AA36-312EEE9DBEF4", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );

    private static class Aftershock extends MobEffect {
        public Aftershock(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    private static class Satisfaction extends MobEffect {
        public Satisfaction(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    private static class Coughing extends MobEffect {
        public Coughing(MobEffectCategory category, int color) {
            super(category, color);
        }

        @Override
        public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
            livingEntity.hurt(livingEntity.damageSources().generic(), amplifier+1);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplifier) {
            int j = 25 >> amplifier;
            if (j > 0) {
                return duration % j == 0;
            } else {
                return true;
            }
        }
    }

    private static class Panic extends MobEffect {
        public Panic(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
