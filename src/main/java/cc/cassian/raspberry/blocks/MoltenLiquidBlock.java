package cc.cassian.raspberry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.RegistryObject;

public class MoltenLiquidBlock extends LiquidBlock {
    public MoltenLiquidBlock(RegistryObject<FlowingFluid> fluid, BlockBehaviour.Properties strength) {
        super(fluid, strength);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        entity.hurt(DamageSource.LAVA, 2f);
        entity.setSecondsOnFire(10);
    }
}
