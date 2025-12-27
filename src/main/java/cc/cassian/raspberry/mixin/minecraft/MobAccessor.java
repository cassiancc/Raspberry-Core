package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public interface MobAccessor {
    @Accessor("leashInfoTag")
    @Nullable
    CompoundTag raspberry$getLeashInfoTag();

    @Accessor("leashInfoTag")
    void raspberry$setLeashInfoTag(@Nullable CompoundTag tag);

    @Accessor("delayedLeashHolderId")
    int raspberry$getDelayedLeashHolderId();

    @Accessor("delayedLeashHolderId")
    void raspberry$setDelayedLeashHolderId(int id);
}