package cc.cassian.raspberry.config;
import cc.cassian.raspberry.RaspberryMod;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private static ModConfig INSTANCE = new ModConfig();
    //General settings
    public boolean aftershock = true;
    public boolean stovesStartLit = false;
    public boolean campfiresStartLit = false;
    public boolean braziersStartLit = false;
    public boolean hideWorldVersion = true;
    public boolean hideTooltips = true;
    public boolean gliders_disableLightning = true;
    public boolean gliders_disableNetherDamage = true;
    public int aquaculture_badBaitLureBonus = 1;
    public int aquaculture_midBaitLureBonus = 2;
    public int aquaculture_goodBaitLureBonus = 3;
    public int aquaculture_wormDiscoveryRange = 80;
    public boolean create_blastproofing = true;
    public boolean overlay_enable = true;
    public boolean overlay_searchContainers = true;
    public boolean overlay_requireItemInHand = false;
    public String overlay_x_colour = "Red";
    public String overlay_y_colour = "Green";
    public String overlay_z_colour = "Blue";
    public int overlay_position_vertical = 90;

    public static void load() {
        if (!Files.exists(configPath())) {
            save();
            return;
        }

        try (var input = Files.newInputStream(configPath())) {
            INSTANCE = GSON.fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), ModConfig.class);
        } catch (IOException e) {
            RaspberryMod.LOGGER.warn("Unable to load config file!");
        }
    }

    public static void save() {
        try (var output = Files.newOutputStream(configPath()); var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            RaspberryMod.LOGGER.warn("Unable to save config file!");
        }
    }

    public static ModConfig get() {
        if (INSTANCE == null) INSTANCE = new ModConfig();
        return INSTANCE;
    }

    public static Path configPath() {
        return Path.of(FMLPaths.CONFIGDIR.get() + "/raspberry_core.json");
    }


}