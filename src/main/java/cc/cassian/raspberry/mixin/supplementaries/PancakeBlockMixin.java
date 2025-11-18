package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.blocks.LemonPancakeBlock;
import cc.cassian.raspberry.compat.NeapolitanCompat;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PancakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static cc.cassian.raspberry.blocks.LemonPancakeBlock.LEMON_TOPPING;
import static net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties.TOPPING;

@Mixin(PancakeBlock.class)
public class PancakeBlockMixin {

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private int toppingsGiveMoreFood(int foodLevelModifier, @Local BlockState state) {
        if (state.getValue(TOPPING) != ModBlockProperties.Topping.NONE || state.getOptionalValue(LEMON_TOPPING).orElse(false))
            return 4;
        return 3;
    }

    @WrapMethod(method = "use")
    private InteractionResult convertLemonPancake(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, Operation<InteractionResult> original) {
        var lemonPancake = LemonPancakeBlock.place(state, worldIn, pos, player, handIn, hit);
        if (lemonPancake != InteractionResult.PASS) {
            return lemonPancake;
        }
        return original.call(state, worldIn, pos, player, handIn, hit);
    }

    @WrapMethod(method = "removeLayer", remap = false)
    private void eatLemonPancake(BlockState state, BlockPos pos, Level world, Player player, Operation<Void> original) {
        if (state.getOptionalValue(LEMON_TOPPING).orElse(false)) {
            LemonPancakeBlock.removeLayer(state, pos, world, player);
        } else {
            original.call(state, pos, world, player);
        }
    }

    @ModifyArg(method = "removeLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;I)V"))
    private MobEffect pancakesGiveSugarRush(MobEffect par1) {
        if (ModCompat.NEAPOLITAN) {
            return NeapolitanCompat.sugarRush();
        } else {
            return par1;
        }
    }
}
