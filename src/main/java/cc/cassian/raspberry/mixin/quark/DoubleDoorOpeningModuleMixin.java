package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.tweaks.module.DoubleDoorOpeningModule;

@Mixin(DoubleDoorOpeningModule.class)
public class DoubleDoorOpeningModuleMixin {
    @WrapOperation(
            method = "tryOpen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getMaterial()Lnet/minecraft/world/level/material/Material;")
    )
    private static Material copperDoorsAreFine(BlockState instance, Operation<Material> original) {
        if (instance.is(RaspberryTags.INTERACTABLE_METAL_DOORS)) {
            return Material.WOOD;
        }
        else return original.call(instance);
    }
}
