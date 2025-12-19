package cc.cassian.raspberry.mixin.ecologics;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import samebutdifferent.ecologics.entity.Penguin;

import cc.cassian.raspberry.config.ModConfig;

@Pseudo
@Mixin(Penguin.class)
public class PenguinMixin {

    @Redirect(
            method = "ageBoundaryReached",
            at = @At(
                    value = "INVOKE",
                    target = "Lsamebutdifferent/ecologics/entity/Penguin;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"
            )
    )
    private ItemEntity preventFeatherShedding(Penguin instance, ItemLike itemLike, int i) {
        if (ModConfig.get().disablePenguinShedding) {
            return null;
        }

        return instance.spawnAtLocation(itemLike, i);
    }
}