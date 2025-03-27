package cc.cassian.raspberry;

import net.minecraftforge.fml.ModList;

public final class ModCompat {
    public static final boolean ANOTHER_FURNITURE;
    public static final boolean AQUACULTURE;
    public static final boolean CLOTH_CONFIG;
    public static final boolean COFH_CORE;
    public static final boolean COPPERIZED;
    public static final boolean COPPER_BACKPORT;
    public static final boolean ENVIRONMENTAL;
    public static final boolean FARMERS_DELIGHT;
    public static final boolean GLIDERS;
    public static final boolean OREGANIZED;
    public static final boolean MAP_ATLASES;
    public static final boolean NEAPOLITAN;
    public static final boolean QUARK;
    public static final boolean SURVIVALITY;
    public static final boolean SUPPLEMENTARIES;
    public static final boolean SPELUNKERY;
    public static final boolean CAVERNS_AND_CHASMS;

    static {
        var mods = ModList.get();
        ANOTHER_FURNITURE = mods.isLoaded("another_furniture");
        AQUACULTURE = mods.isLoaded("aquaculture");
        CAVERNS_AND_CHASMS = mods.isLoaded("caverns_and_chasms");
        CLOTH_CONFIG = mods.isLoaded("cloth_config");
        COFH_CORE = mods.isLoaded("cofh_core");
        COPPERIZED = mods.isLoaded("copperized");
        COPPER_BACKPORT = mods.isLoaded("copperandtuffbackport");
        ENVIRONMENTAL = mods.isLoaded("environmental");
        FARMERS_DELIGHT = mods.isLoaded("farmersdelight");
        GLIDERS = mods.isLoaded("gliders");
        MAP_ATLASES = mods.isLoaded("map_atlases");
        NEAPOLITAN = mods.isLoaded("neapolitan");
        OREGANIZED = mods.isLoaded("oreganized");
        QUARK = mods.isLoaded("quark");
        SURVIVALITY = mods.isLoaded("survivality");
        SUPPLEMENTARIES = mods.isLoaded("supplementaries");
        SPELUNKERY = mods.isLoaded("spelunkery");
    }
}
