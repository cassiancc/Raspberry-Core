package cc.cassian.raspberry.mixin.windswept;

import com.rosemods.windswept.common.levelgen.feature.PineTreeFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PineTreeFeature.class)
public class PineTreeFeatureMixin {

    @Unique
    private static final TagKey<Biome> HIGH_PINECONE_RATE = TagKey.create(Registries.BIOME, new ResourceLocation("raspberry", "has_dense_pinecones"));

    @ModifyConstant(method = "doPlace", constant = @Constant(intValue = 8, ordinal = 1), remap = false)
    private int modifyPineconeChance(int original, FeaturePlaceContext<TreeConfiguration> context) {
        Holder<Biome> biome = context.level().getBiome(context.origin());

        if (biome.is(HIGH_PINECONE_RATE)) {
            return 2; // TODO: original method uses nextInt(8), so 2 is 50%. adjust?
        }
        return original;
    }
}