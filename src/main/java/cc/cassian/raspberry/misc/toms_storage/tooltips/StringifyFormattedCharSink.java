package cc.cassian.raspberry.misc.toms_storage.tooltips;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

import javax.annotation.Nonnull;

public class StringifyFormattedCharSink implements FormattedCharSink {
    private static final char REPLACEMENT_CHARACTER = '\ufffd';

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public boolean accept(int i, @Nonnull Style style, int charCode) {
        char c = (char) charCode;
        if (style.isObfuscated() && c != ' ') {
            // The idea is that you are able to search for obfuscated text through regex,
            // either through `.` or `[^chars]`.
            c = REPLACEMENT_CHARACTER;
        }

        stringBuilder.append(c);

        return true;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
