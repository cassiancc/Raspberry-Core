package cc.cassian.raspberry;

import net.minecraftforge.fml.ModList;

public final class ModCompat {

    private static boolean isLoaded(String modId) {
        return ModList.get() != null && ModList.get().isLoaded(modId);
    }

    public static boolean hasAllurement() { return isLoaded("allurement"); }
    public static boolean hasAnotherFurniture() { return isLoaded("another_furniture"); }
    public static boolean hasAquaculture() { return isLoaded("aquaculture"); }
    public static boolean hasBetterBeacons() { return isLoaded("better_beacons"); }
    public static boolean hasCavernsAndChasms() { return isLoaded("caverns_and_chasms"); }
    public static boolean hasClothConfig() { return isLoaded("cloth_config"); }
    public static boolean hasCofhCore() { return isLoaded("cofh_core"); }
    public static boolean hasCopperized() { return isLoaded("copperized"); }
    public static boolean hasCopperBackport() { return isLoaded("copperandtuffbackport"); }
    public static boolean hasCreate() { return isLoaded("create"); }
    public static boolean hasDomesticationInnovation() { return isLoaded("domesticationinnovation"); }
    public static boolean hasEnsorcellation() { return isLoaded("ensorcellation"); }
    public static boolean hasEnvironmental() { return isLoaded("environmental"); }
    public static boolean hasFarmersDelight() { return isLoaded("farmersdelight"); }
    public static boolean hasGliders() { return isLoaded("vc_gliders"); }
    public static boolean hasImmersiveOverlays() { return isLoaded("immersiveoverlays"); }
    public static boolean hasOreganized() { return isLoaded("oreganized"); }
    public static boolean hasMapAtlases() { return isLoaded("map_atlases"); }
    public static boolean hasMiningMaster() { return isLoaded("miningmaster"); }
    public static boolean hasNeapolitan() { return isLoaded("neapolitan"); }
    public static boolean hasQuark() { return isLoaded("quark"); }
    public static boolean hasSurvivality() { return isLoaded("survivality"); }
    public static boolean hasSupplementaries() { return isLoaded("supplementaries"); }
    public static boolean hasSpelunkery() { return isLoaded("spelunkery"); }
    public static boolean hasTomsStorage() { return isLoaded("toms_storage"); }
    public static boolean hasBlueprint() { return isLoaded("blueprint"); }
    public static boolean hasBrewinAndChewin() { return isLoaded("brewinandchewin"); }
    public static boolean hasEmi() { return isLoaded("emi"); }
    public static boolean hasControllable() { return isLoaded("controllable"); }
    public static boolean hasNaturalist() { return isLoaded("naturalist"); }
    public static boolean hasItemObliterator() { return isLoaded("item_obliterator"); }
    public static boolean hasSidekick() { return isLoaded("sidekick"); }
    public static boolean hasXaerosWorldMap() { return isLoaded("xaeroworldmap"); }
}