package cc.cassian.raspberry.mixin.etched;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Allay.class)
public abstract class AllayMixin extends PathfinderMob {

    @Shadow
    @Nullable
    private BlockPos jukeboxPos;

    protected AllayMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;of([Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/item/crafting/Ingredient;"))
    private static Ingredient raspberry$isDuplicationItem(ItemLike[] items, Operation<Ingredient> original) {
       return Ingredient.of(RaspberryTags.DUPLICATES_ALLAYS);
    }

    @Inject(method = "shouldStopDancing", at = @At("HEAD"), cancellable = true)
    private void raspberry$shouldStopDancing(CallbackInfoReturnable<Boolean> cir) {
        if (this.jukeboxPos != null) {
            ResourceLocation blockId = Registry.BLOCK.getKey(this.level.getBlockState(this.jukeboxPos).getBlock());
            
            if (blockId.toString().equals("etched:album_jukebox")) {
                if (this.jukeboxPos.closerToCenterThan(this.position(), GameEvent.JUKEBOX_PLAY.getNotificationRadius())) {
                    cir.setReturnValue(false); 
                }
            }
        }
    }
}