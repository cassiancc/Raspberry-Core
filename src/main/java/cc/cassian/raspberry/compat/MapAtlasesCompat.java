package cc.cassian.raspberry.compat;

import pepjebs.mapatlases.config.MapAtlasesClientConfig;

public class MapAtlasesCompat {
    public static boolean showingCoords() {
        return MapAtlasesClientConfig.drawMinimapCoords.get();
    }

    public static boolean isInCorner() {
        return MapAtlasesClientConfig.miniMapAnchoring.get().isUp && !MapAtlasesClientConfig.miniMapAnchoring.get().isLeft;
    }
}
