package cc.cassian.raspberry.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(HolderSet.Named.class)
public interface HolderSetNamedAccessor {
    @Accessor("contents")
    List<Holder<?>> getContents();

    @Accessor("contents")
    @Mutable
    void setContents(List<Holder<?>> contents);
}