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

package cc.cassian.raspberry.compat.oreganized.world;

import cc.cassian.raspberry.compat.oreganized.network.packet.KineticHitPacket;
import cc.cassian.raspberry.registry.RaspberryAttributes;
import galena.oreganized.network.OreganizedNetwork;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class KineticDamage {

    public static void apply(LivingEntity cause, Entity target) {
        if (cause == null) return;

        var stack = cause.getMainHandItem();
        var mods = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(RaspberryAttributes.KINETIC_DAMAGE.get());

        if (mods.isEmpty()) return;
        if (!(cause instanceof IMotionHolder motionHolder)) return;

        var motion = Math.sqrt(motionHolder.oreganised$getMotion()) - 0.15;

        var factor = Math.min(motion / 0.12, 1F);
        if (factor <= 0.0) return;

        // ignores modifier operation, since only addition is used by oreganized this works, but may be adapted in the future
        var kineticDamage = factor * mods.stream().mapToDouble(AttributeModifier::getAmount).sum();
        var source = DamageSource.GENERIC;

        if (kineticDamage == 0.0) return;

        target.invulnerableTime = 0;
        target.hurt(source, (float) kineticDamage);

        if (ServerLifecycleHooks.getCurrentServer() != null)
            OreganizedNetwork.CHANNEL.send(
                    PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(target.getX(), target.getY(), target.getZ(), 16.0, target.level.dimension())),
                    new KineticHitPacket(target.getId(), factor)
            );
    }

    public static void spawnParticles(Entity target, double factor) {
        var level = target.level;
        var count = (int) (1 + Math.floor(4 * factor));

        for (int i = 0; i < count; i++) {
//            level.addParticle(
//                    OParticleTypes.KINETIC_HIT.get(),
//                    target.getRandomX(0.75), target.getRandomY(), target.getRandomZ(0.75),
//                    level.random.nextGaussian() * 0.02D, level.random.nextGaussian() * 0.02D, level.random.nextGaussian() * 0.02D
//            );
        }
    }
}
