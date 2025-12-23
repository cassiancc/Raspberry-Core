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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public boolean thrownItemParticles = true;
    public boolean gliders_disableLightning = true;
    public boolean gliders_disableNetherDamage = true;
    public int aquaculture_badBaitLureBonus = 1;
    public int aquaculture_midBaitLureBonus = 2;
    public int aquaculture_goodBaitLureBonus = 3;
    public int aquaculture_wormDiscoveryRange = 80;
    public boolean create_blastproofing = true;
    public boolean searchContainers = true;
    public boolean horses_noWander = true;
    public boolean horses_noBuck = true;
    public boolean horses_stepHeight = true;
    public boolean foxes_noRabbitDrops = true;
    public boolean noRabbitFootDrops = true;
    public boolean raspberry_beacon_interaction = false;
    public boolean emi_tablets = true;
    public boolean toms_hideBeacon = true;
    public int mirrorSearchRadius = 24;
    public int mirrorVerticalSearchRadius = 12;
    public int mirrorParticleSearchRadius = 48;
    public int mirrorVerticalParticleSearchRadius = 24;
    public double mirrorVolumeModifier = 0.4;
    public boolean sunSensitiveRaiders = false;
    public boolean mapsWorkInInventory = false;
    public boolean create_emi = false;
    public boolean better_haste = true;
    public boolean bannerlessRaiders = false;
    public boolean naturalist_stackableItems = true;
    public double rose_gold_bomb_knockback = 1.5;
    public int rose_gold_arrow_knockback = 2;
    public double rose_gold_arrow_velocity = 1.5;
    public boolean unified_wrenches = false;
    public boolean saferKnightJump = true;
    public boolean raspberryCartEngine = true;
    public double raspberryCartMaxSpeed = 34.0;
    public boolean dungeons_mobs_revised_cobwebs = true;
    public boolean ghastDragonFireball = false;
    public boolean leashFences = true; // TODO: reimplement harou's leashed fences
    public boolean disableBirchLeafTinting = true;
    public boolean disableMapleLeafTinting = false;
    public boolean jadeRequiresScoping = true;
    public MusicFrequency musicFrequency = MusicFrequency.DEFAULT;
    public boolean showMusicToast = false;
    public boolean disableFaucetSourceBlocks = false;
    public boolean betterJukeboxes = true;
    public double jukeboxDistance = 64.0;
    public boolean disablePenguinShedding = false;
    public boolean fastFlyBlockBreaking = true;

    public List<String> hiddenEnchantments = new ArrayList<>();
    public List<String> hiddenPotions = new ArrayList<>();
    public List<String> hiddenTooltipItems = new ArrayList<>();
    public Map<String, String> creativeTabIcons = new HashMap<>();
    public boolean noTemplates = true;

	public ModConfig() {
        creativeTabIcons.put("minecraft:building_blocks", "minecraft:bricks");
        creativeTabIcons.put("minecraft:colored_blocks", "minecraft:cyan_wool");
        creativeTabIcons.put("minecraft:natural_blocks", "minecraft:grass_block");
        creativeTabIcons.put("minecraft:functional_blocks", "minecraft:oak_sign");
        creativeTabIcons.put("minecraft:redstone_blocks", "minecraft:redstone");
        creativeTabIcons.put("minecraft:tools_and_utilities", "minecraft:diamond_pickaxe");
        creativeTabIcons.put("minecraft:combat", "minecraft:netherite_sword");
        creativeTabIcons.put("minecraft:food_and_drinks", "minecraft:golden_apple");
        creativeTabIcons.put("minecraft:ingredients", "minecraft:iron_ingot");
        creativeTabIcons.put("minecraft:spawn_eggs", "minecraft:creeper_spawn_egg");
        creativeTabIcons.put("minecraft:op_blocks", "minecraft:command_block");
    }

    public static void load() {
        if (!Files.exists(configPath())) {
            save();
            return;
        }

        try (var input = Files.newInputStream(configPath())) {
            INSTANCE = GSON.fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), ModConfig.class);
            save();
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