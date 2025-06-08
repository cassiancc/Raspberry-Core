package cc.cassian.raspberry.compat.toms_storage.filters;

import com.tom.storagemod.platform.Platform;
import com.tom.storagemod.util.StoredItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ModIdFilter implements Predicate<StoredItemStack> {
    private final Pattern pattern;

    private static String getModId(StoredItemStack stack) {
        return ForgeRegistries.ITEMS.getKey(stack.getStack().getItem()).getNamespace();
    }

    public ModIdFilter(Pattern patterns) {
        this.pattern = patterns;
    }

    @Override
    public boolean test(StoredItemStack storedItemStack) {
        String modId = getModId(storedItemStack);
        return this.pattern.matcher(modId).find();
    }
}
