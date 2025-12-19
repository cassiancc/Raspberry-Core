package cc.cassian.raspberry.mixin.etched;

import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AlbumJukeboxBlockEntity.class)
public class AlbumJukeboxBlockEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private static void raspberry$tick(Level level, BlockPos pos, BlockState state, AlbumJukeboxBlockEntity entity, CallbackInfo ci) {
        if (level != null && !level.isClientSide() && entity.isPlaying()) {
            if (level.getGameTime() % 20 == 0) {
                int radius = GameEvent.JUKEBOX_PLAY.getNotificationRadius();
                AABB aabb = (new AABB(pos)).inflate(radius);
                List<Allay> list = level.getEntitiesOfClass(Allay.class, aabb);
                for (Allay allay : list) {
                    if (!allay.isDancing()) {
                        allay.setJukeboxPlaying(pos, true);
                    }
                }
            }
        }
    }
}