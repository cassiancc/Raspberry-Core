package cc.cassian.raspberry;

import cc.cassian.raspberry.compat.QuarkCompat;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import net.mehvahdjukaar.supplementaries.common.block.blocks.CandleHolderBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;
import static cc.cassian.raspberry.registry.RaspberryTags.*;

public class ModHelpers {

    // Automatically generate translation keys for config options.
    public static Component fieldName(Field field) {
        return Component.translatable("config."+MOD_ID+".config." + field.getName());
    }

    // Get the current value of a config field.
    @SuppressWarnings("unchecked")
    public static <T> T fieldGet(Object instance, Field field) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Set a config field.
    public static <T> Consumer<T> fieldSetter(Object instance, Field field) {
        return t -> {
            try {
                field.set(instance, t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Used to check what colour the text should be.
     * Adapted from Item Descriptions.
     */
    public static char getColour(String colour) {
        int length = colour.length();
        if (length == 1) {
            return colour.charAt(0);
        }
        else {
            String replacedColour = colour.toLowerCase().replace(" ", "_");
            return switch (replacedColour) {
                case "black", "dark_blue", "dark_green", "dark_red", "dark_purple",
                     "blue", "green", "aqua", "red", "yellow", "white" ->
                        Objects.requireNonNullElse(ChatFormatting.getByName(colour), ChatFormatting.GRAY).getChar();
                case "pink", "light_purple" ->
                        Objects.requireNonNullElse(ChatFormatting.getByName("light_purple"), ChatFormatting.GRAY).getChar();
                case "dark_gray", "dark_grey" ->
                        Objects.requireNonNullElse(ChatFormatting.getByName("dark_gray"), ChatFormatting.GRAY).getChar();
                case "cyan", "dark_aqua" ->
                        Objects.requireNonNullElse(ChatFormatting.getByName("dark_aqua"), ChatFormatting.GRAY).getChar();
                case "orange", "gold", "dark_yellow" ->
                        Objects.requireNonNullElse(ChatFormatting.getByName("gold"), ChatFormatting.GRAY).getChar();
                default -> ChatFormatting.GRAY.getChar();
            };

        }
    }

    public static SoundEvent getSoundForItem(ItemStack stack, SoundEvent original) {
        if (stack.is(CRUNCHY_FRUIT_SOUNDS)) {
            return RaspberrySoundEvents.CRUNCHY_FRUIT_SOUNDS.get();
        } else if (stack.is(DRIED_KELP_SOUNDS)) {
            return RaspberrySoundEvents.DRIED_KELP_SOUNDS.get();
        } else if (stack.is(SOFT_FRUIT_SOUNDS)) {
            return RaspberrySoundEvents.SOFT_FRUIT_SOUNDS.get();
        } else if (stack.is(STEW_SOUNDS)) {
            return RaspberrySoundEvents.STEW_SOUNDS.get();
        } else if (stack.is(VEGETABLE_SOUNDS)) {
            return RaspberrySoundEvents.VEGETABLE_SOUNDS.get();
        } else if (stack.is(GENERIC_SOUNDS)) {
            return SoundEvents.GENERIC_EAT;
        } else if (stack.is(DRINK_SOUNDS)) {
            return SoundEvents.GENERIC_DRINK;
        } else if (stack.is(HONEY_SOUNDS)) {
            return SoundEvents.HONEY_DRINK;
        }
        return original;
    }

    public static boolean shouldWoodPostChainConnect(BlockState downState) {
        if(downState.getBlock() instanceof CookingPotBlock && downState.getValue(CookingPotBlock.SUPPORT).equals(CookingPotSupport.HANDLE)) {
            return true;
        }
        else if (downState.getBlock() instanceof CandleHolderBlock && downState.getValue(BlockStateProperties.ATTACH_FACE).equals(AttachFace.CEILING)) {
            return true;
        }
        else return (ModCompat.QUARK && QuarkCompat.isPaperLantern(downState));
    }
}