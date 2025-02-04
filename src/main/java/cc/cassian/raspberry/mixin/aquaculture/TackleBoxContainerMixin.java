package cc.cassian.raspberry.mixin.aquaculture;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teammetallurgy.aquaculture.api.AquacultureAPI;
import com.teammetallurgy.aquaculture.block.blockentity.TackleBoxBlockEntity;
import com.teammetallurgy.aquaculture.inventory.container.TackleBoxContainer;
import com.teammetallurgy.aquaculture.inventory.container.slot.SlotFishingRod;
import com.teammetallurgy.aquaculture.inventory.container.slot.SlotHidable;
import com.teammetallurgy.aquaculture.item.HookItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Pseudo
@Mixin(TackleBoxContainer.class)
public abstract class TackleBoxContainerMixin extends AbstractContainerMenu {
    protected TackleBoxContainerMixin(@Nullable MenuType<?> arg, int i) {
        super(arg, i);
    }

    @Shadow
    public Slot slotHook;
    @Shadow
    public Slot slotBait;
    @Shadow
    public Slot slotLine;
    @Shadow
    public Slot slotBobber;
    @Shadow
    private int rows;
    @Shadow
    private int collumns;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V"))
    private void modifyTackleBoxContainer(LazyOptional<IItemHandler> instance, NonNullConsumer<? super IItemHandler> consumer, Operation<Void> original) {
        instance.ifPresent(handler -> {
            SlotFishingRod fishingRod = (SlotFishingRod)this.addSlot(new SlotFishingRod(handler, 0, 117, 21));

            this.slotHook = this.addSlot(new SlotHidable(fishingRod, 0, 106, 44) {
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return stack.getItem() instanceof HookItem && super.mayPlace(stack);
                }
            });

            this.slotBait = this.addSlot(new SlotHidable(fishingRod, 1, 129, 44) {
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return stack.is(RaspberryTags.BAIT) && super.mayPlace(stack);
                }
            });

            this.slotLine = this.addSlot(new SlotHidable(fishingRod, 2, 106, 67) {
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    boolean isDyeable = stack.getItem() instanceof DyeableLeatherItem;
                    return stack.is(AquacultureAPI.Tags.FISHING_LINE) && isDyeable && super.mayPlace(stack);
                }
            });

            this.slotBobber = this.addSlot(new SlotHidable(fishingRod, 3, 129, 67) {
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    boolean isDyeable = stack.getItem() instanceof DyeableLeatherItem;
                    return stack.is(AquacultureAPI.Tags.BOBBER) && isDyeable && super.mayPlace(stack);
                }
            });

            for(int column = 0; column < this.collumns; ++column) {
                for(int row = 0; row < this.rows; ++row) {
                    this.addSlot(new SlotItemHandler(handler, 1 + row + column * this.collumns, 8 + row * 18, 8 + column * 18) {
                        public boolean mayPlace(@Nonnull ItemStack stack) {
                            return TackleBoxBlockEntity.canBePutInTackleBox(stack);
                        }
                    });
                }
            }
        });
    }

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void makeBaitNormal(int slotId, int dragType, ClickType clickType, Player player, CallbackInfo ci) {
        super.clicked(slotId, dragType, clickType, player);
        ci.cancel();
    }
}
