package cc.cassian.raspberry.mixin.connectiblechains;

import com.lilypuree.connectiblechains.client.render.entity.ChainKnotEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChainKnotEntityRenderer.class)
public class ChainKnotEntityRendererMixin { 

    @Unique
    private static final ResourceLocation RASPBERRY_KNOT_TEXTURE = new ResourceLocation("raspberry", "textures/entity/chain_knot.png");

    @Inject(method = "getKnotTexture", at = @At("HEAD"), cancellable = true, remap = false)
    private void raspberry$overrideKnotTexture(Block block, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(RASPBERRY_KNOT_TEXTURE);
    }
}