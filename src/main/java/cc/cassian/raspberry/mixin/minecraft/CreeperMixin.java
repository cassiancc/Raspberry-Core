package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Mob {

    protected CreeperMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;getSwellDir()I"))
    private int getSwell(Creeper instance, Operation<Integer> original) {
        Integer swellDir = original.call(instance);
        if (ModConfig.get().creepersDoNotExplodeInMidAir && !instance.isOnGround() && swellDir>0) {
            swellDir = -swellDir;
        }
        return swellDir;
    }

}
