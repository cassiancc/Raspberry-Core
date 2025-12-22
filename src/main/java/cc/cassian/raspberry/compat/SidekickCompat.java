package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.compat.emi.EmiCompat;
import com.unascribed.sidekick.client.screen.SidekickScreen;
import dev.emi.emi.api.EmiRegistry;

public class SidekickCompat {
	public static void addDragAndDrop(EmiRegistry emiRegistry) {
		emiRegistry.addDragDropHandler(SidekickScreen.class, EmiCompat::handleDragAndDrop);
	}
}
