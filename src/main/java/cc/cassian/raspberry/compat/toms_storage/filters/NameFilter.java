package cc.cassian.raspberry.compat.toms_storage.filters;

import com.tom.storagemod.util.StoredItemStack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class NameFilter implements Predicate<StoredItemStack> {
    private final Pattern pattern;

    public NameFilter(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean test(StoredItemStack storedItemStack) {
        ItemStack stack = storedItemStack.getStack();

        String realName = I18n.get(stack.getDescriptionId()).toLowerCase();
        if (this.pattern.matcher(realName).find()) return true;

        String displayName = stack.getDisplayName().getString().toLowerCase();
        return this.pattern.matcher(displayName).find();
    }
}
