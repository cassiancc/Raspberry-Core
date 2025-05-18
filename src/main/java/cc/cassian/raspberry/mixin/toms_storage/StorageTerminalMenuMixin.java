package cc.cassian.raspberry.mixin.toms_storage;

import cc.cassian.raspberry.misc.toms_storage.StorageTerminalHelper;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tom.storagemod.gui.StorageTerminalMenu;
import com.tom.storagemod.util.StoredItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(value = StorageTerminalMenu.class, remap = false)
public abstract class StorageTerminalMenuMixin {
    @Shadow
    public List<StoredItemStack> itemListClientSorted;

    @Shadow
    protected int lines;

    @Shadow
    protected abstract void setSlotContents(int id, StoredItemStack stack);

    private void setItemsUnsafe(float arg) {
        List<StoredItemStack> itemList = this.itemListClientSorted;

        int i = (itemList.size() + 9 - 1) / 9 - this.lines;
        int j = (int)((double)(arg * (float)i) + (double)0.5F);
        if (j < 0) {
            j = 0;
        }

        for (int k = 0; k < this.lines; ++k) {
            for (int l = 0; l < 9; ++l) {
                int i1 = l + (k + j) * 9;
                if (i1 >= 0 && i1 < itemList.size()) {
                    this.setSlotContents(l + k * 9, itemList.get(i1));
                } else {
                    this.setSlotContents(l + k * 9, null);
                }
            }
        }
    }

    @WrapMethod(method = "scrollTo")
    private void scrollTo(float arg, Operation<Void> original) {
        ReentrantReadWriteLock.ReadLock readLock = StorageTerminalHelper.rwLock.readLock();

        try {
            readLock.lock();
            setItemsUnsafe(arg);
        } finally {
            readLock.unlock();
        }
    }
}
