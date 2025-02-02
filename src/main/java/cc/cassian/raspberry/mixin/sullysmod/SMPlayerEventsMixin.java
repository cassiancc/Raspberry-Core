package cc.cassian.raspberry.mixin.sullysmod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.uraneptus.sullysmod.core.events.SMPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Pseudo
@Mixin(SMPlayerEvents.class)
public class SMPlayerEventsMixin {
    @Inject(
            method = "onRightClickBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock;setCanceled(Z)V"),
            remap = false
    )
    private static void fixParticles(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci, @Local Level level, @Local BlockPos pos, @Local RandomSource random, @Local(ordinal = 1) ItemStack itemInHand) {
        ParticleUtils.spawnParticlesOnBlockFace(level, pos, new ItemParticleOption(ParticleTypes.ITEM, itemInHand), UniformInt.of(1, 2), event.getFace(), () -> {
            return new Vec3(Mth.nextDouble(random, -0.05, 0.05), 0.0, Mth.nextDouble(random, -0.05, 0.05));
        }, 0.55);
    }

    @WrapOperation(
            method = "onRightClickBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ParticleUtils;spawnParticlesOnBlockFace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/util/valueproviders/IntProvider;Lnet/minecraft/core/Direction;Ljava/util/function/Supplier;D)V", ordinal = 1))
    private static void replaceParticles(Level i, BlockPos j, ParticleOptions arg, IntProvider arg2, Direction arg3, Supplier<Vec3> arg4, double arg5, Operation<Void> original) {
        // you get NOTHING good DAY sir
    }
}
