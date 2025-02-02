package cc.cassian.raspberry.mixin.environmental;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.teamabnormals.environmental.core.registry.EnvironmentalItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.teamabnormals.environmental.core.other.EnvironmentalTiers.Armor;

import java.util.UUID;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Shadow @Final protected ArmorMaterial material;
    @Unique
    private static final UUID ENVIRONMENTAL_YAK_PANTS_UUID = UUID.fromString("35e7342c-9ff3-40ea-b72e-7a2c29c12caa");

    @Inject(method = "getDefaultAttributeModifiers", at = @At(value = "RETURN"), cancellable = true)
    public void yakAttributeModifier(EquipmentSlot arg, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        if (EquipmentSlot.LEGS.equals(arg) && material.equals(Armor.YAK)) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(cir.getReturnValue());
            builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(ENVIRONMENTAL_YAK_PANTS_UUID, "Step Height", 0.4F, AttributeModifier.Operation.ADDITION));
            cir.setReturnValue(builder.build());
        }
    }
}
