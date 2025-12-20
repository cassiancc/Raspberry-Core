package cc.cassian.raspberry.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.cassian.raspberry.ModHelpers.*;

public class ClothConfigFactory {

    private static final ModConfig DEFAULT_VALUES = new ModConfig();

    public static Screen create(Screen parent) {
        final var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.raspberry.title"));

        final var entryBuilder = builder.entryBuilder();

        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.title"));
        ConfigCategory gliderCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.gliders"));
        ConfigCategory aquacultureCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.aquaculture"));
        ConfigCategory horseCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.horses"));
        ConfigCategory oreganizedCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.oreganized"));
        ConfigCategory hiddenCategory = builder.getOrCreateCategory(Component.translatable("config.raspberry.hidden"));

        hiddenCategory.addEntry(entryBuilder.startStrList(Component.translatable("config.raspberry.config.hidden_enchantments"), ModConfig.get().hiddenEnchantments)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.raspberry.config.hidden_enchantments_tooltip"))
                .setSaveConsumer(list -> ModConfig.get().hiddenEnchantments = list)
                .build());

        hiddenCategory.addEntry(entryBuilder.startStrList(Component.translatable("config.raspberry.config.hidden_potions"), ModConfig.get().hiddenPotions)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.raspberry.config.hidden_potions_tooltip"))
                .setSaveConsumer(list -> ModConfig.get().hiddenPotions = list)
                .build());

        hiddenCategory.addEntry(entryBuilder.startStrList(Component.translatable("config.raspberry.config.hidden_tooltips"), ModConfig.get().hiddenTooltipItems)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.raspberry.config.hidden_tooltip_tooltip"))
                .setSaveConsumer(list -> ModConfig.get().hiddenTooltipItems = list)
                .build());

        List<String> iconDefaults = DEFAULT_VALUES.creativeTabIcons.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());

        List<String> currentIcons = ModConfig.get().creativeTabIcons.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());

        hiddenCategory.addEntry(entryBuilder.startStrList(Component.translatable("config.raspberry.config.creative_tab_icons"), currentIcons)
                .setDefaultValue(iconDefaults)
                .setTooltip(Component.translatable("config.raspberry.config.creative_tab_icons_tooltip"))
                .setSaveConsumer(list -> {
                    ModConfig.get().creativeTabIcons.clear();
                    for (String entry : list) {
                        String[] split = entry.split("=");
                        if (split.length == 2) {
                            ModConfig.get().creativeTabIcons.put(split[0].trim(), split[1].trim());
                        }
                    }
                })
                .build());

        for (var field : ModConfig.class.getFields()) {
            if (field.getType() == List.class || field.getType() == Map.class) continue;

            ConfigCategory category;
            if (field.getName().equals("jukeboxDistance")) continue;
            if (field.getName().equals("showMusicToast")) continue;
            if (field.getName().contains("gliders")) category = gliderCategory;
            else if (field.getName().contains("aquaculture")) category = aquacultureCategory;
            else if (field.getName().contains("horse")) category = horseCategory;
            else if (field.getName().contains("mirror")) category = oreganizedCategory;
            else category = generalCategory;

            if (field.getType() == boolean.class) {
                category.addEntry(entryBuilder.startBooleanToggle(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((boolean) fieldGet(DEFAULT_VALUES, field)).build());

            }
            else if (field.getType() == String.class) {
                category.addEntry(entryBuilder.startStrField(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((String) fieldGet(DEFAULT_VALUES, field)).build());
            }
            else if (field.getType() == int.class) {
                category.addEntry(entryBuilder.startIntField(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((int) fieldGet(DEFAULT_VALUES, field)).build());
            }
            else if (field.getType() == double.class) {
                category.addEntry(entryBuilder.startDoubleField(fieldName(field), fieldGet(ModConfig.get(), field))
                        .setSaveConsumer(fieldSetter(ModConfig.get(), field))
                        .setDefaultValue((double) fieldGet(DEFAULT_VALUES, field)).build());
            }
        }
        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}