package cc.cassian.raspberry.mixin;

import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(GreatbladeSlashEntity.class)
public abstract class GreatbladeSlashEntityMixin extends Entity {
    @Shadow public ItemStack swordStack;
    @Unique
    private boolean raspberryCore$swordDamaged = false;

    public GreatbladeSlashEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/jsburg/clash/weapons/GreatbladeItem;onSlashHit(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"))
    private void hurtStack(GreatbladeItem instance, ItemStack stack, LivingEntity target, Entity user, Operation<Void> original) {
        original.call(instance, stack, target, user);
        if (!raspberryCore$swordDamaged && user instanceof LivingEntity living) {
            this.swordStack.getItem().hurtEnemy(stack, target, living);
            this.raspberryCore$swordDamaged = true;
        }
    }
}
