package cc.cassian.raspberry.mixin.supplementaries;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.mehvahdjukaar.supplementaries.integration.forge.quark.CartographersQuillItem;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CartographersQuillItem.class)
public class CartographersQuillItemMixin {
    @Final
    @Mutable
    @Shadow
    public static String TAG_NAME;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void fixTagName(CallbackInfo info) {
        TAG_NAME = "name";
    }

    @WrapMethod(method = "getMapName", remap = false)
    private String fixTagName(CompoundTag tag, Operation<String> original) {
        return tag.contains("name") ? tag.getString("name") : null;
    }

    @WrapOperation(method = "forStructure(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/HolderSet;IZILnet/minecraft/world/level/saveddata/maps/MapDecoration$Type;Ljava/lang/String;I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 1))
    private static void fixTagName(CompoundTag instance, String key, String value, Operation<Void> original) {
        instance.putString("name", value);
    }

}
