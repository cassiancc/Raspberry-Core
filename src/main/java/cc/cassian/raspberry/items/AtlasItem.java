package cc.cassian.raspberry.items;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.XaerosCompat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AtlasItem extends Item {
    public AtlasItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (ModCompat.hasXaerosWorldMap() && level.isClientSide()) {
            XaerosCompat.openWorldMap(player);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
