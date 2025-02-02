package cc.cassian.raspberry.mixin.farmersdelight;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.utility.MathUtils;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelMixin {
    @Inject(
            method = "useOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CampfireBlock;canLight(Lnet/minecraft/world/level/block/state/BlockState;)Z"),
            cancellable = true)
    private void lightStovesWithTwigs(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var level = context.getLevel();
        var state = level.getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof StoveBlock && !state.getValue(BlockStateProperties.LIT)) {
            level.playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, MathUtils.RAND.nextFloat() * 0.4F + 0.8F);
            level.setBlock(context.getClickedPos(), state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
