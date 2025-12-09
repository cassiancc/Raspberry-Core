package cc.cassian.raspberry.items;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.XaerosCompat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AtlasItem extends Item {
    public AtlasItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (ModCompat.XAEROS_WORLD_MAP && level.isClientSide()) {
            XaerosCompat.openWorldMap(player);
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
