package cc.cassian.raspberry.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

@Mixin(RichSoilFarmlandBlock.class)
public class RichSoilFarmlandBlockMixin {
    // Make Rich Soil Farmland not negate all fall damage.
    // This restores the implementation of Block::fallOn.
    @Inject(at = @At(value = "HEAD"), method = "fallOn")
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        entity.causeFallDamage(fallDistance, 1.0F, DamageSource.FALL);
    }
}
