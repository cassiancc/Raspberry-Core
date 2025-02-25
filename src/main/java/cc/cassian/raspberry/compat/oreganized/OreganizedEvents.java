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

import cc.cassian.raspberry.registry.RaspberryAttributes;
import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
}
