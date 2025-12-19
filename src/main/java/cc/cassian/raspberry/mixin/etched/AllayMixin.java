package cc.cassian.raspberry.mixin.etched;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Allay.class)
public abstract class AllayMixin extends PathfinderMob {

    @Shadow
    @Nullable
    private BlockPos jukeboxPos;

    protected AllayMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "shouldStopDancing", at = @At("HEAD"), cancellable = true)
    private void raspberry$shouldStopDancing(CallbackInfoReturnable<Boolean> cir) {
        if (this.jukeboxPos != null) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(this.level().getBlockState(this.jukeboxPos).getBlock());
            
            if (blockId.toString().equals("etched:album_jukebox")) {
                if (this.jukeboxPos.closerToCenterThan(this.position(), GameEvent.JUKEBOX_PLAY.getNotificationRadius())) {
                    cir.setReturnValue(false); 
                }
            }
        }
    }
}