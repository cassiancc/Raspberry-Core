package cc.cassian.raspberry.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import xaero.map.WorldMapSession;
import xaero.map.gui.GuiMap;

public class XaerosCompat {
    public static void openWorldMap(Player player) {
        Minecraft.getInstance().setScreen(new GuiMap(null, null, WorldMapSession.getCurrentSession().getMapProcessor(), player));
    }
}
