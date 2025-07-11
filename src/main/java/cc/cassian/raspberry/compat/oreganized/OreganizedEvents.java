/*
 * Copyright (c) 2022 Team Galena

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cc.cassian.raspberry.compat.oreganized;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryAttributes;
import cc.cassian.raspberry.registry.RaspberryParticleTypes;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import cc.cassian.raspberry.registry.RaspberryTags;
import galena.oreganized.content.item.SilverMirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.UUID;

public class OreganizedEvents {
    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        var stack = event.getItemStack();
        var mods = event.getModifiers();

        if (event.getSlotType() != EquipmentSlot.MAINHAND) return;

        if (stack.is(RaspberryTags.HAS_KINETIC_DAMAGE) && !mods.containsKey(RaspberryAttributes.KINETIC_DAMAGE.get())) {
            var damage = stack.getItem() instanceof DiggerItem item
                    ? item.getAttackDamage()
                    : stack.getItem() instanceof SwordItem item
                    ? item.getDamage()
                    : 2.0F;
            event.addModifier(RaspberryAttributes.KINETIC_DAMAGE.get(), new AttributeModifier(
                    UUID.fromString("0191ff58-54d7-711d-8a94-692379277c24"), "Kinetic Damage", damage / 3, AttributeModifier.Operation.ADDITION)
            );
        }
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(event.getSource().getDirectEntity() instanceof Player perp)) return;
        if (!victim.isInvertedHealAndHarm()) return;
        if (!(perp.getMainHandItem().getItem() instanceof SilverMirrorItem)) return;

        Level level = victim.level;
        BlockPos origin = victim.blockPosition();
        BlockPos closestSilver = findNearestSilverBlock(level, origin);
        if (closestSilver == null) return;
        Vec3 targetPos = Vec3.atCenterOf(closestSilver);
        Vec3 velocity = targetPos.subtract(victim.position()).normalize();
        level.playSound((Player) null, perp.getX(), perp.getY(), perp.getZ(), RaspberrySoundEvents.SILVER_HIT.get(), perp.getSoundSource(),
                (float) ModConfig.get().mirrorVolumeModifier, 1.0F + (float) (perp.getRandom().nextGaussian() * 0.35));

        for (int i = 0; i < 8; i++) {
            double offsetX = (victim.getRandom().nextDouble() - (0.5 * victim.getBbWidth())) * 1.5;
            double offsetY = victim.getRandom().nextDouble() * victim.getBbHeight();
            double offsetZ = (victim.getRandom().nextDouble() - (0.5 * victim.getBbWidth())) * 1.5;
            Vec3 finalVelocity = velocity.scale(victim.getRandom().nextDouble() * 0.225);

            Vec3 spawnPos = victim.position().add(offsetX, offsetY, offsetZ);
            level.addParticle(RaspberryParticleTypes.MIRROR.get(),
                    spawnPos.x, spawnPos.y, spawnPos.z,
                    finalVelocity.x, finalVelocity.y, finalVelocity.z);
        }
    }

    @Nullable
    private static BlockPos findNearestSilverBlock(Level level, BlockPos origin) {
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        BlockPos closest = null;
        double closestDistSq = Double.MAX_VALUE;

        int radius = ModConfig.get().mirrorParticleSearchRadius;
        int verticalRadius = ModConfig.get().mirrorVerticalParticleSearchRadius;

        int radiusSq = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -verticalRadius; dy <= verticalRadius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

                    searchPos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!level.isLoaded(searchPos)) continue;
                    if (!level.getBlockState(searchPos).is(RaspberryTags.MIRROR_DETECTABLES)) continue;

                    double distSq = searchPos.distSqr(origin);
                    if (distSq < closestDistSq) {
                        closestDistSq = distSq;
                        closest = searchPos.immutable();
                    }
                }
            }
        }
        return closest;
    }

}
