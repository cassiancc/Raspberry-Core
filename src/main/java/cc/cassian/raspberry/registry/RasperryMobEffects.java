package cc.cassian.raspberry.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RasperryMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);
    public static final RegistryObject<MobEffect> THUNDER_SYNERGY = MOB_EFFECTS.register("thunder_synergy",
            () -> new ThunderSynergy( MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "CE4EFE3F-12D8-4C0A-AA36-312EEE9DBEF3", 0.2F, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, "CE4EFE3F-12D8-4C0A-AA36-5BA2BB9FFFF3", 0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL)
    );

    private static class ThunderSynergy extends MobEffect {
        public ThunderSynergy(MobEffectCategory category, int color) {
            super(category, color);
        }
    }
}
