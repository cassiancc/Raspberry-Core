package cc.cassian.raspberry.mixin.environmental;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, priority = 1500)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At(value = "TAIL", target = "Lnet/example/Dummy;dummy()V"))
    private void mixin(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        Iterable<ItemStack> armorSlots = this.getArmorSlots();
        for (ItemStack stack : armorSlots) {
            if (stack.is(RaspberryTags.DISABLED)) {
                this.setItemSlot(getEquipmentSlotForItem(stack), Items.AIR.getDefaultInstance());
            }
        }

    }
}
