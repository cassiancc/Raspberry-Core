package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.client.MusicEventListener;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "reload", at = @At("TAIL"))
    private void addMusicListener(CallbackInfo ci) {
        ((SoundManager) (Object) this).addListener(new MusicEventListener());
    }
}