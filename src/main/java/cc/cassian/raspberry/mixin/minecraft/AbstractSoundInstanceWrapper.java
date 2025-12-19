package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceWrapper {

    @Accessor("attenuation")
    void setAttenuationType(SoundInstance.Attenuation attenuationType);

    @Accessor("relative")
    void setRelative(boolean isRelative);

    @Accessor("volume")
    void trackVolumeForReferenceOnly(float volume);

    @Accessor("x")
    void setX(double x);

    @Accessor("y")
    void setY(double y);

    @Accessor("z")
    void setZ(double z);
}