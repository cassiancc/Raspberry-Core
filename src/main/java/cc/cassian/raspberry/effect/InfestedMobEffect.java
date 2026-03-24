//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cc.cassian.raspberry.effect;

import java.util.function.ToIntFunction;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class InfestedMobEffect extends MobEffect {
	private static final float chanceToSpawn = ModConfig.get().infested_spawn_chance;
	private static final ToIntFunction<RandomSource> spawnedCount = (randomSource) -> Mth.randomBetweenInclusive(randomSource, 1, 2);

	public InfestedMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public static void onMobHurt(LivingHurtEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity.hasEffect(RaspberryMobEffects.INFESTED.get()) && livingEntity.getRandom().nextFloat() <= chanceToSpawn) {
			int count = spawnedCount.applyAsInt(livingEntity.getRandom());

			for (int i = 0; i < count; ++i) {
				spawnSilverfish(livingEntity.level, livingEntity, livingEntity.getX(), livingEntity.getY() + (double) livingEntity.getBbHeight() / (double) 2.0F, livingEntity.getZ());
			}
		}
	}

	private static void spawnSilverfish(Level level, LivingEntity entity, double x, double y, double z) {
		Silverfish silverfish = EntityType.SILVERFISH.create(level);
		if (silverfish != null) {
			RandomSource randomSource = entity.getRandom();
			float angle = ((float)Math.PI / 2F);
			float randomAngle = Mth.randomBetween(randomSource, (-angle), angle);
			Vec3 viewDirection = entity.getLookAngle().multiply(0.3F, 0.3F, 0.3F).multiply(1.0F, 1.5F, 1.0F).yRot(randomAngle);
			silverfish.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
			silverfish.setDeltaMovement(viewDirection);
			level.addFreshEntity(silverfish);
			silverfish.playSound(SoundEvents.SILVERFISH_HURT);
		}
	}
}
