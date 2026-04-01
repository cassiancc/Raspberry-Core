package cc.cassian.raspberry;

import cc.cassian.raspberry.entity.GrapplingHookEntity;

import javax.annotation.Nullable;

public interface PlayerWithGrapplingHook {
    @Nullable GrapplingHookEntity raspberryCore$getHook();

    void raspberryCore$setHook(@Nullable GrapplingHookEntity hookEntity);
}
