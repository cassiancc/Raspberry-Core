package cc.cassian.raspberry.mixin.farmersdelight;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.item.SkilletItem;

@Mixin(SkilletItem.class)
public class SkilletItemMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Tiers;getAttackDamageBonus()F"))
    private float mixin(Tiers instance, Operation<Float> original, Block block, Item.Properties properties) {
        if (block.equals(RaspberryBlocks.SKILLET.get())) return Tiers.NETHERITE.getAttackDamageBonus();
        return original.call(instance);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Tiers;getUses()I"))
    private static int mixin2(Tiers instance, Operation<Integer> original, Block block, Item.Properties properties) {
        if (block.equals(RaspberryBlocks.SKILLET.get())) return Tiers.NETHERITE.getUses();
        return original.call(instance);
    }
}
