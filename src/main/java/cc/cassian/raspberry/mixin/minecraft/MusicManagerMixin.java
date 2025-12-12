package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MusicManager.class)
public class MusicManagerMixin {

    @WrapOperation(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I")
    )
    private int modifySongDelay(RandomSource random, int min, int max, Operation<Integer> original) {
        int vanillaDelay = original.call(random, min, max);

        MusicFrequency frequency = ModConfig.get().musicFrequency;

        if (frequency == MusicFrequency.CONSTANT) {
            return 20; 
        } else if (frequency == MusicFrequency.FREQUENT) {
            return (int) (vanillaDelay * 0.5f);
        }

        return vanillaDelay;
    }
}