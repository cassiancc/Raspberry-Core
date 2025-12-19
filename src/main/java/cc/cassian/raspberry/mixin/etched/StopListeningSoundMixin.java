package cc.cassian.raspberry.mixin.etched;

import cc.cassian.raspberry.mixin.minecraft.AbstractSoundInstanceWrapper;
import gg.moonflower.etched.api.sound.StopListeningSound;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StopListeningSound.class)
public abstract class StopListeningSoundMixin implements AbstractSoundInstanceWrapper {

    @Shadow(remap = false)
    public abstract SoundInstance getParent();

    @Override
    public void setAttenuationType(SoundInstance.Attenuation attenuationType) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.setAttenuationType(attenuationType);
        }
    }

    @Override
    public void setRelative(boolean isRelative) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.setRelative(isRelative);
        }
    }

    @Override
    public void trackVolumeForReferenceOnly(float volume) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.trackVolumeForReferenceOnly(volume);
        }
    }

    @Override
    public void setX(double x) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.setX(x);
        }
    }

    @Override
    public void setY(double y) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.setY(y);
        }
    }

    @Override
    public void setZ(double z) {
        if (this.getParent() instanceof AbstractSoundInstanceWrapper wrapper) {
            wrapper.setZ(z);
        }
    }
}