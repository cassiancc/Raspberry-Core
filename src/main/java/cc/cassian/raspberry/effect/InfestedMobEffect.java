//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cc.cassian.raspberry.effect;

import java.util.function.ToIntFunction;

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
	private final float chanceToSpawn;
	private final ToIntFunction<RandomSource> spawnedCount;

	public InfestedMobEffect(MobEffectCategory category, int color, float chanceToSpawn, ToIntFunction<RandomSource> spawnedCount) {
		super(category, color);
		this.chanceToSpawn = chanceToSpawn;
		this.spawnedCount = spawnedCount;
	}

	public static void onMobHurt(LivingHurtEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity.hasEffect(RaspberryMobEffects.INFESTED.get())) {
			InfestedMobEffect effect = (InfestedMobEffect) livingEntity.getEffect(RaspberryMobEffects.INFESTED.get()).getEffect();
			if (livingEntity.getRandom().nextFloat() <= effect.chanceToSpawn) {
				int i = effect.spawnedCount.applyAsInt(livingEntity.getRandom());

				for(int j = 0; j < i; ++j) {
					effect.spawnSilverfish(livingEntity.level, livingEntity, livingEntity.getX(), livingEntity.getY() + (double)livingEntity.getBbHeight() / (double)2.0F, livingEntity.getZ());
				}
			}
		}
	}

	private void spawnSilverfish(Level level, LivingEntity entity, double x, double y, double z) {
		Silverfish silverfish = EntityType.SILVERFISH.create(level);
		if (silverfish != null) {
			RandomSource randomSource = entity.getRandom();
			float f = ((float)Math.PI / 2F);
			float g = Mth.randomBetween(randomSource, (-f), f);
			Vec3 vector3f = entity.getLookAngle().multiply(0.3F, 0.3F, 0.3F).multiply(1.0F, 1.5F, 1.0F).yRot(g);
			silverfish.moveTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
			silverfish.setDeltaMovement(vector3f);
			level.addFreshEntity(silverfish);
			silverfish.playSound(SoundEvents.SILVERFISH_HURT);
		}
	}
}
