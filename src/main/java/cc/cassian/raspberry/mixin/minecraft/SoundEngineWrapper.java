package cc.cassian.raspberry.mixin.minecraft;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.audio.Listener;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(SoundEngine.class)
public interface SoundEngineWrapper {
    @Accessor
    boolean isLoaded();

    @Accessor
    Map<SoundInstance, ChannelAccess.ChannelHandle> getInstanceToChannel();

    @Accessor
    Multimap<SoundSource, SoundInstance> getInstanceBySource();

    @Accessor
    Listener getListener();

    @Invoker("calculateVolume")
    float calculateAdjustedVolume(float volume, SoundSource category);
}