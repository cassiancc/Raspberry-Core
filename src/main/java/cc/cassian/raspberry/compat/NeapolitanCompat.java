package cc.cassian.raspberry.compat;

import com.teamabnormals.neapolitan.core.registry.NeapolitanMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class NeapolitanCompat {
    private static final UUID STEP_HEIGHT_UUID = UUID.fromString("35e7342c-9ff3-40ea-b72e-7a2c29c12caa");

    public static void boostAgility() {
        NeapolitanMobEffects.AGILITY.get().addAttributeModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_UUID.toString(), 0.4F, AttributeModifier.Operation.ADDITION);
    }

    public static MobEffect sugarRush() {
        return NeapolitanMobEffects.SUGAR_RUSH.get();
    }
}
