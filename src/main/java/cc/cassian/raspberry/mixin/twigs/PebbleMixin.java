package cc.cassian.raspberry.mixin.twigs;

import cc.cassian.raspberry.config.ModConfig;
import com.ninni.twigs.entity.Pebble;
import net.mehvahdjukaar.supplementaries.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pebble.class)
public abstract class PebbleMixin extends ThrowableItemProjectile {

    public PebbleMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "onHit",
            at = @At(value = "INVOKE", target = "Lcom/ninni/twigs/entity/Pebble;getItem()Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    private void rewriteRarity(HitResult hitResult, CallbackInfo ci) {
        if (ModConfig.get().strongerPebbles && hitResult instanceof BlockHitResult blockHitResult) {
            if (raspberryCore$breakGlass(blockHitResult.getBlockPos(), 6))
                ci.cancel();
        }
    }

    @Unique
	private boolean raspberryCore$breakGlass(BlockPos pos, int chance) {
        boolean flag = false;
        int c = chance - 1 - this.random.nextInt(4);
        BlockState state = this.level().getBlockState(pos);
        if (!(state.getBlock().getExplosionResistance() > 3.0F)) {
            if (c >= 0 && state.is(ModTags.BRICK_BREAKABLE_GLASS)) {
                this.level().destroyBlock(pos, true);
                this.raspberryCore$breakGlass(pos.above(), c);
                this.raspberryCore$breakGlass(pos.below(), c);
                this.raspberryCore$breakGlass(pos.east(), c);
                this.raspberryCore$breakGlass(pos.west(), c);
                this.raspberryCore$breakGlass(pos.north(), c);
                this.raspberryCore$breakGlass(pos.south(), c);
                flag = true;
            }
        }
        return flag;
    }
}
