package cc.cassian.raspberry.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.math.Quaternion;
import net.minecraft.client.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SingleQuadParticle.class)
public class SingleQuadParticleMixin {
    @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 0), name = "quaternion")
    private Quaternion removeRotation(Quaternion cameraRotation, @Local(name = "f") float x,@Local(name = "g") float y, @Local(name = "h") float z ) {
        if ((Object) this instanceof FlameParticle) {
            float length = (float) Math.sqrt(cameraRotation.j() * cameraRotation.j() + cameraRotation.r() * cameraRotation.r());
            return new Quaternion(0, cameraRotation.j() / length, 0, cameraRotation.r() / length);
        }
        return cameraRotation;
    }
}
