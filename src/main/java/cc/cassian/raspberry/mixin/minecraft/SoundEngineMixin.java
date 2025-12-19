/* Copyright (c) 2025 WatDuhHekBro

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
*/

package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Unique
    private static final double MIN_DISTANCE = 50.0;
    @Unique
    private static final double MAX_DISTANCE = 100.0;
    @Unique
    private static final double MIN_DISTANCE_SQUARED = MIN_DISTANCE * MIN_DISTANCE;
    @Unique
    private static final double MAX_DISTANCE_SQUARED = MAX_DISTANCE * MAX_DISTANCE;
    @Unique
    private static final double DIVISOR = MAX_DISTANCE_SQUARED - MIN_DISTANCE_SQUARED;
    @Unique
    private static final int TICKS_TO_FULLY_FADE_OUT = 20;
    @Unique
    private static final int TICKS_TO_FULLY_FADE_IN = 40;
    @Unique
    private static final float MUSIC_VOLUME_PER_TICK_TO_FADE_OUT = 1f / TICKS_TO_FULLY_FADE_OUT;
    @Unique
    private static final float MUSIC_VOLUME_PER_TICK_TO_FADE_IN = 1f / TICKS_TO_FULLY_FADE_IN;
    @Unique
    private static final Map<SoundInstance, Vec3> coordinates = new HashMap<>();
    @Unique
    private final SoundEngineWrapper wrapper = (SoundEngineWrapper) this;
    @Unique
    private float currentMusicVolumeFactor = 1;
    @Unique
    private boolean wasMusicPaused = false;

    @Inject(method = "play", at = @At("HEAD"))
    private void injectPlay(SoundInstance sound, CallbackInfo ci) {
        if (!ModConfig.get().betterJukeboxes) return;

        if (sound.getSource() == SoundSource.RECORDS && sound instanceof AbstractSoundInstanceWrapper modifiedSound) {
            modifiedSound.setRelative(true);
            modifiedSound.setAttenuationType(SoundInstance.Attenuation.NONE);

            coordinates.put(sound, new Vec3(sound.getX(), sound.getY(), sound.getZ()));
            modifiedSound.setX(0);
            modifiedSound.setY(0);
            modifiedSound.setZ(0);
        }
    }

    @Inject(method = "tick(Z)V", at = @At("HEAD"))
    private void injectTick(boolean isPaused, CallbackInfo ci) {
        if (!ModConfig.get().betterJukeboxes) {
            if (currentMusicVolumeFactor != 1.0f || wasMusicPaused) {
                currentMusicVolumeFactor = 1.0f;
                raspberryCore$setMusicVolumeAndHandlePausing();
                wasMusicPaused = false;
            }
            return;
        }

        Collection<SoundInstance> records = wrapper.getInstanceBySource().get(SoundSource.RECORDS);
        long amountRecordsHearable = records.stream().filter(sound -> sound.getVolume() > 0).count();
        Vec3 playerPosition = wrapper.getListener().getListenerPosition();

        if (amountRecordsHearable > 0) {
            raspberryCore$musicFadeOut();
        } else {
            raspberryCore$musicFadeIn();
        }

        for (SoundInstance sound : records) {
            if (coordinates.containsKey(sound)) {
                double distanceSquared = playerPosition.distanceToSqr(coordinates.get(sound));
                double calculatedVolume = (MAX_DISTANCE_SQUARED - distanceSquared) / DIVISOR;
                calculatedVolume = Math.max(0, Math.min(1, calculatedVolume));
                float adjustedVolume = wrapper.calculateAdjustedVolume((float) calculatedVolume, SoundSource.RECORDS);

                wrapper.getInstanceToChannel().get(sound).execute(source -> source.setVolume(adjustedVolume));

                if (sound instanceof AbstractSoundInstanceWrapper modifiedSound) {
                    modifiedSound.trackVolumeForReferenceOnly(adjustedVolume);
                }
            }

            ChannelAccess.ChannelHandle sourceManager = wrapper.getInstanceToChannel().get(sound);

            if (sourceManager.isStopped()) {
                coordinates.remove(sound);
            }
        }
    }

    @Unique
    private void raspberryCore$setMusicVolumeAndHandlePausing() {
        if (!wrapper.isLoaded()) {
            return;
        }

        Collection<SoundInstance> music = wrapper.getInstanceBySource().get(SoundSource.MUSIC);

        for (SoundInstance sound : music) {
            ChannelAccess.ChannelHandle sourceManager = wrapper.getInstanceToChannel().get(sound);
            float maxVolume = sound.getVolume(); // Usually 1.0 for C418 music and 0.4 for newer music

            sourceManager.execute(source -> {
                source.setVolume(wrapper.calculateAdjustedVolume(maxVolume * currentMusicVolumeFactor, SoundSource.MUSIC));

                if (currentMusicVolumeFactor <= 0 && !wasMusicPaused) {
                    sourceManager.execute(Channel::pause);
                    wasMusicPaused = true;
                } else if (currentMusicVolumeFactor > 0 && wasMusicPaused) {
                    sourceManager.execute(Channel::unpause);
                    wasMusicPaused = false;
                }
            });
        }
    }

    @Unique
    private void raspberryCore$musicFadeOut() {
        if (currentMusicVolumeFactor > 0) {
            currentMusicVolumeFactor = Math.max(currentMusicVolumeFactor - MUSIC_VOLUME_PER_TICK_TO_FADE_OUT, 0);
            raspberryCore$setMusicVolumeAndHandlePausing();
        } else {
            raspberryCore$pauseMusic();
        }
    }

    @Unique
    private void raspberryCore$musicFadeIn() {
        if (currentMusicVolumeFactor < 1) {
            currentMusicVolumeFactor = Math.min(currentMusicVolumeFactor + MUSIC_VOLUME_PER_TICK_TO_FADE_IN, 1);
            raspberryCore$setMusicVolumeAndHandlePausing();
        }
    }

    @Unique
    private void raspberryCore$pauseMusic() {
        if (!wrapper.isLoaded()) {
            return;
        }

        Collection<SoundInstance> music = wrapper.getInstanceBySource().get(SoundSource.MUSIC);

        for (SoundInstance sound : music) {
            ChannelAccess.ChannelHandle sourceManager = wrapper.getInstanceToChannel().get(sound);
            sourceManager.execute(Channel::pause);
        }
    }
}