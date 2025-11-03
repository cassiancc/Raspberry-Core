package cc.cassian.raspberry.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class BlockSupplier {
    private final String blockID;
    private final RegistryObject<Block> block;
    private final RegistryObject<BlockItem> item;

    public BlockSupplier(String blockID, RegistryObject<Block> block, RegistryObject<BlockItem> item) {
        this.blockID = blockID;
        this.block = block;
        this.item = item;
    }

    public RegistryObject<Block> getBlockSupplier() {
        return block;
    }

    public Block getBlock() {
        return block.get();
    }

    public RegistryObject<BlockItem> getItemSupplier() {
        return item;
    }

    public BlockState defaultBlockState() {
        return block.get().defaultBlockState();
    }

    public String getID() {
        return blockID;
    }
}
