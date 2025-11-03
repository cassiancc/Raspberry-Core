package cc.cassian.raspberry.mixin.miningmaster;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.controllable.ControllableCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.miningmaster.network.DamageKnightJumpPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(DamageKnightJumpPacket.class)
public class DamageKnightJumpPacketMixin {
    @WrapMethod(method = "lambda$handle$1")
    private static void durabilityDropChance(Supplier context, DamageKnightJumpPacket message, Operation<Void> original) {
        Random random = new Random();
        if (random.nextBoolean() || !ModConfig.get().saferKnightJump) {
            original.call(context, message);
        }
    }
}
