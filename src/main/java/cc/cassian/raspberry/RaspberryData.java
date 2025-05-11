package cc.cassian.raspberry;

import com.teamabnormals.blueprint.common.world.storage.tracking.DataProcessors;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedData;
import com.teamabnormals.blueprint.common.world.storage.tracking.TrackedDataManager;
import net.minecraft.core.BlockPos;

import static cc.cassian.raspberry.RaspberryMod.locate;

public class RaspberryData {
    public static final TrackedData<Integer> WORM_HUNTING_TIME = TrackedData.Builder.create(DataProcessors.INT, () -> 0).enableSaving().build();
    public static final TrackedData<Integer> SNIFF_SOUND_TIME = TrackedData.Builder.create(DataProcessors.INT, () -> 0).build();
    public static final TrackedData<BlockPos> WORM_POS = TrackedData.Builder.create(DataProcessors.POS, () -> BlockPos.ZERO).enableSaving().build();
    public static final TrackedData<Boolean> HAS_WORM_TARGET = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).enableSaving().build();
    public static final TrackedData<Boolean> LOOKING_FOR_WORM = TrackedData.Builder.create(DataProcessors.BOOLEAN, () -> false).build();

    public static void registerData() {
        TrackedDataManager.INSTANCE.registerData(locate("truffle_hunting_time"), WORM_HUNTING_TIME);
        TrackedDataManager.INSTANCE.registerData(locate("sniff_sound_time"), SNIFF_SOUND_TIME);
        TrackedDataManager.INSTANCE.registerData(locate( "truffle_pos"), WORM_POS);
        TrackedDataManager.INSTANCE.registerData(locate( "has_truffle_target"), HAS_WORM_TARGET);
        TrackedDataManager.INSTANCE.registerData(locate("looking_for_truffle"), LOOKING_FOR_WORM);
    }
}
