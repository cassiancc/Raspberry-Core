package cc.cassian.raspberry.client.tooltips;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

import javax.annotation.Nonnull;

public class StringifyFormattedCharSink implements FormattedCharSink {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public boolean accept(int i, @Nonnull Style style, int charCode) {
        // "Inspired" by net.minecraft.client.gui.Font$StringRenderOutput
        if (!style.isObfuscated() && charCode != 32) {
            stringBuilder.append((char) charCode);
        }

        return true;
    }

    @Override
    public String toString() {
        System.out.println("StringifyFormattedCharSink: " + stringBuilder.toString());
        return stringBuilder.toString();
    }
}
