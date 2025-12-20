package cc.cassian.raspberry.mixin.feature_recyler;

import dev.corgitaco.featurerecycler.FeatureRecycler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.StoveBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static cc.cassian.raspberry.RaspberryMod.LOGGER;

@Mixin(FeatureRecycler.class)
public class FeatureRecyclerMixin {
    /**
     * @author cassiancc
     * @reason prevent hard crash with RF - see <a href="https://github.com/CorgiTaco-MC/Feature-Recycler/pull/5">issue</a>
     */
    @Overwrite(remap = false)
    public static <T extends Holder<Biome>> List<FeatureSorter.StepFeatureData> recycle(List<T> biomes, Function<T, List<HolderSet<PlacedFeature>>> toFeatueSetFunction) {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Starting feature recycler...");

        int crashesPrevented = 0;

        List<Map<T, List<Holder<PlacedFeature>>>> biomeTracker = new ArrayList<>();

        GenerationStep.Decoration[] values = GenerationStep.Decoration.values();
        for (int i = 0; i < values.length; i++) {
            biomeTracker.add(new Reference2ObjectLinkedOpenHashMap<>());
        }

        for (T biome : biomes) {
            List<HolderSet<PlacedFeature>> features = toFeatueSetFunction.apply(biome).stream().distinct().toList();

            for (int i = 0; i < features.size(); i++) {
                HolderSet<PlacedFeature> feature = features.get(i);
                biomeTracker.get(i).put(biome, new ArrayList<>(feature.stream().toList()));
            }
        }


        for (Map<T, List<Holder<PlacedFeature>>> featuresForBiomeStage : biomeTracker) {
            for (int biomeIdx = 0; biomeIdx < biomes.size(); biomeIdx++) {
                T biome = biomes.get(biomeIdx);
                List<Holder<PlacedFeature>> currentList = featuresForBiomeStage.get(biome);
                if (currentList == null) {
                    continue;
                }

                for (int currentHolderIndex = 0; currentHolderIndex < currentList.size(); currentHolderIndex++) {
                    Holder<PlacedFeature> currentHolder = currentList.get(currentHolderIndex);

                    for (int nextHolderIndex = currentHolderIndex + 1; nextHolderIndex < currentList.size(); nextHolderIndex++) {
                        Holder<PlacedFeature> nextHolder = currentList.get(nextHolderIndex);
                        int currentFeatureIDX = -1;
                        int nextFeatureIDX = -1;
                        Holder<Biome> biomeRuleSetter = null;

                        for (int previousBiomeIdx = 0; previousBiomeIdx < biomeIdx - 1; previousBiomeIdx++) {
                            T previousBiome = biomes.get(previousBiomeIdx);
                            List<Holder<PlacedFeature>> previousBiomeStageData = featuresForBiomeStage.get(previousBiome);
                            if (previousBiomeStageData == null) {
                                continue;
                            }

                            if (currentFeatureIDX >= 0 && nextFeatureIDX >= 0) {
                                break;
                            }

                            int previousBiomeCurrentFeatureIDX = -1;
                            int previousBiomeNextFeatureIDX = -1;
                            for (int previousBiomeHolderIdx = 0; previousBiomeHolderIdx < previousBiomeStageData.size(); previousBiomeHolderIdx++) {
                                Holder<PlacedFeature> previousBiomePlacedFeatureHolder = previousBiomeStageData.get(previousBiomeHolderIdx);

                                if (previousBiomePlacedFeatureHolder == currentHolder) {
                                    previousBiomeCurrentFeatureIDX = previousBiomeHolderIdx;
                                }

                                if (previousBiomePlacedFeatureHolder == nextHolder) {
                                    previousBiomeNextFeatureIDX = previousBiomeHolderIdx;
                                }


                                if (previousBiomeCurrentFeatureIDX >= 0 && previousBiomeNextFeatureIDX >= 0) {
                                    break;
                                }
                            }

                            currentFeatureIDX = previousBiomeCurrentFeatureIDX;
                            nextFeatureIDX = previousBiomeNextFeatureIDX;
                            biomeRuleSetter = previousBiome;
                        }
                        if (currentFeatureIDX >= 0 && nextFeatureIDX >= 0) {
                            if (currentFeatureIDX > nextFeatureIDX) {
                                ResourceLocation currentBiomeLocation = biome.unwrapKey().isEmpty() ? null : biome.unwrapKey().orElseThrow().location();
                                String currentBiomeName = currentBiomeLocation == null ? "???" : currentBiomeLocation.toString();
                                String currentFeatureName = currentHolder.unwrapKey().isEmpty() ? "???" : currentHolder.unwrapKey().orElseThrow().location().toString();
                                String nextFeatureName = nextHolder.unwrapKey().isEmpty() ? "???" : nextHolder.unwrapKey().orElseThrow().location().toString();
                                ResourceLocation ruleSetterLocation = biomeRuleSetter.unwrapKey().isEmpty() ? null : biomeRuleSetter.unwrapKey().orElseThrow().location();
                                String biomeRuleSetterName = ruleSetterLocation == null ? "???" :  ruleSetterLocation.toString();

                                LOGGER.warn("Moved placed feature \"%s\" from index %d to index %d for biome \"%s\". Placed Feature index rules set by biome \"%s\".".formatted(currentFeatureName, currentHolderIndex, nextHolderIndex, currentBiomeName, biomeRuleSetterName));
                                LOGGER.warn("Moved placed feature \"%s\" from index %d to index %d for biome \"%s\". Placed Feature index rules set by biome \"%s\".".formatted(nextFeatureName, nextHolderIndex, currentHolderIndex, currentBiomeName, biomeRuleSetterName));


                                LOGGER.warn("Just prevented a crash between %s and %s! Please report the issues to their respective issue trackers.".formatted(currentBiomeLocation == null ? "???" : currentBiomeLocation.getNamespace(), ruleSetterLocation == null ? "???" : ruleSetterLocation.getNamespace()));
                                crashesPrevented++;
                                currentList.set(currentHolderIndex, nextHolder);
                                currentList.set(nextHolderIndex, currentHolder);
                            }
                        }
                    }
                }
            }
        }

        List<FeatureSorter.StepFeatureData> steps = new ArrayList<>();

        biomeTracker.forEach(stepData -> {
            List<PlacedFeature> organizedFeatures = new ArrayList<>();

            Object2IntOpenHashMap<PlacedFeature> indexGetter = new Object2IntOpenHashMap<>();

            int idx = 0;
            for (List<Holder<PlacedFeature>> value : stepData.values()) {
                for (Holder<PlacedFeature> holder : value) {
                    organizedFeatures.add(holder.value());
                    indexGetter.put(holder.value(), idx);
                    idx++;
                }
            }
            steps.add(new FeatureSorter.StepFeatureData(organizedFeatures, indexGetter::getInt));
        });

        LOGGER.info("Finished recycling features. Took %dms".formatted(System.currentTimeMillis() - startTime));

        if (crashesPrevented > 0) {
            LOGGER.info("Feature Recycler just prevented %d crashes!".formatted(crashesPrevented));
        }
        return steps;
    }
}
