package cc.cassian.raspberry.mixin.aquaculture;

import com.teammetallurgy.aquaculture.api.AquacultureAPI;
import com.teammetallurgy.aquaculture.item.AquaFishingRodItem;
import com.teammetallurgy.aquaculture.item.BaitItem;
import com.teammetallurgy.aquaculture.item.HookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Pseudo
@Mixin(targets = "com.teammetallurgy.aquaculture.item.AquaFishingRodItem$FishingRodEquipmentHandler")
public class FishingRodEquipmentHandlerMixin {
    @Shadow @Final @Mutable
    private ItemStackHandler items;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyItemHandler(@Nonnull ItemStack stack, CallbackInfo ci) {
        this.items = new ItemStackHandler(4) {
            public int getSlotLimit(int slot) {
                return slot == 1 ? 64 : 1;
            }

            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getItem() instanceof HookItem;
                    case 1 -> stack.getItem() instanceof BaitItem;
                    case 2 ->
                            stack.is(AquacultureAPI.Tags.FISHING_LINE) && stack.getItem() instanceof DyeableLeatherItem;
                    case 3 -> stack.is(AquacultureAPI.Tags.BOBBER) && stack.getItem() instanceof DyeableLeatherItem;
                    default -> false;
                };
            }

            protected void onContentsChanged(int slot) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.put("Inventory", this.serializeNBT());
                stack.setTag(tag);
            }
        };
    }
}

