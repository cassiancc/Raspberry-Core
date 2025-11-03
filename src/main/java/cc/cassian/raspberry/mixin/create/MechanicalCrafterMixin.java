package cc.cassian.raspberry.mixin.create;

import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.crafter.CrafterCTBehaviour;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Pseudo
@Mixin(CrafterCTBehaviour.class)
public class MechanicalCrafterMixin {
    @Inject(method = "getShift", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void moddedBlocksAreStorage(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite, CallbackInfoReturnable<CTSpriteShiftEntry> cir) {
        if (cir.getReturnValue().equals(AllSpriteShifts.BRASS_CASING)) {
            cir.setReturnValue(AllSpriteShifts.ANDESITE_CASING);
        }
    }
}
