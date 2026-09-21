/*
MIT License

Copyright (c) 2020 vectorwing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

=============================================================================
This software includes code from the NeoForge project
https://github.com/neoforged/NeoForge/
For specifically InvWrapper and ItemStackHandler logic.

NeoForge is licensed under the LGPL 2.1 license.
You may read the license here:
https://github.com/neoforged/NeoForge/blob/1.21.x/LICENSE.txt

Some files may include others' licenses. Please read the top of the file
for information on other licenses.
=============================================================================
 */
package cc.cassian.raspberry.client.model;

import cc.cassian.raspberry.RaspberryMod;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import vectorwing.farmersdelight.client.model.WrappedItemModel;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.*;

/**
 * Credits to the Botania Team for the class reference!
 */

@SuppressWarnings("deprecation")
public class NetheriteSkilletModel implements BakedModel
{
	private final ModelBakery bakery;
	private final BakedModel originalModel;
	private final BakedModel cookingModel;

	public NetheriteSkilletModel(ModelBakery bakery, BakedModel originalModel, BakedModel cookingModel) {
		this.bakery = bakery;
		this.originalModel = Preconditions.checkNotNull(originalModel);
		this.cookingModel = Preconditions.checkNotNull(cookingModel);
	}

	private final ItemOverrides itemOverrides = new ItemOverrides()
	{
		@Nonnull
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entityIn, int seed) {
			CompoundTag tag = stack.getOrCreateTag();

			if (tag.contains("Cooking")) {
				ItemStack ingredientStack = ItemStack.of(tag.getCompound("Cooking"));
				return NetheriteSkilletModel.this.getCookingModel(ingredientStack);
			}

			return originalModel;
		}
	};

	@Nonnull
	@Override
	public ItemOverrides getOverrides() {
		return itemOverrides;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
		return originalModel.getQuads(state, side, rand);
	}

	@Nonnull
	@Override
	public ItemTransforms getTransforms() {
		return originalModel.getTransforms();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return originalModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return originalModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return originalModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return originalModel.isCustomRenderer();
	}

	@Nonnull
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return originalModel.getParticleIcon();
	}

	private final HashMap<Item, CompositeBakedModel> cache = new HashMap<>();

	private CompositeBakedModel getCookingModel(ItemStack ingredientStack) {
		return cache.computeIfAbsent(ingredientStack.getItem(), p -> new CompositeBakedModel(bakery, ingredientStack, cookingModel));
	}

	private static class CompositeBakedModel extends WrappedItemModel<BakedModel>
	{
		private final List<BakedQuad> genQuads = new ArrayList<>();
		private final Map<Direction, List<BakedQuad>> faceQuads = new EnumMap<>(Direction.class);

		public CompositeBakedModel(ModelBakery bakery, ItemStack ingredientStack, BakedModel skillet) {
			super(skillet);

			ResourceLocation ingredientLocation = ForgeRegistries.ITEMS.getKey(ingredientStack.getItem());
			UnbakedModel ingredientUnbaked = bakery.getModel(new ModelResourceLocation(ingredientLocation, "inventory"));
			ModelState transform = new SimpleModelState(
					new Transformation(
							new Vector3f(0.0F, -0.4F, 0.0F),
							Vector3f.XP.rotationDegrees(270),
							new Vector3f(0.625F, 0.625F, 0.625F), null));
			ResourceLocation name = RaspberryMod.locate("netherite_skillet_with_" + ingredientLocation.toString().replace(':', '_'));

			BakedModel ingredientBaked;
			if (ingredientUnbaked instanceof BlockModel bm && ((BlockModel) ingredientUnbaked).getRootModel() == ModelBakery.GENERATION_MARKER) {
				ingredientBaked = new ItemModelGenerator()
						.generateBlockModel(Material::sprite, bm)
						.bake(bakery, bm, Material::sprite, transform, name, false);
			} else {
				ingredientBaked = ingredientUnbaked.bake(bakery, Material::sprite, transform, name);
			}

			for (Direction e : Direction.values()) {
				faceQuads.put(e, new ArrayList<>());
			}

			RandomSource rand = RandomSource.create(0);
			for (BakedModel pass : ingredientBaked.getRenderPasses(ingredientStack, false)) {
				genQuads.addAll(pass.getQuads(null, null, rand, ModelData.EMPTY, null));

				for (Direction e : Direction.values()) {
					rand.setSeed(0);
					faceQuads.get(e).addAll(pass.getQuads(null, e, rand, ModelData.EMPTY, null));
				}
			}

			for (BakedModel pass : skillet.getRenderPasses(ModItems.SKILLET.get().getDefaultInstance(), false)) {
				rand.setSeed(0);
				genQuads.addAll(pass.getQuads(null, null, rand, ModelData.EMPTY, null));
				for (Direction e : Direction.values()) {
					rand.setSeed(0);
					faceQuads.get(e).addAll(pass.getQuads(null, e, rand, ModelData.EMPTY, null));
				}
			}
		}

		@Override
		public boolean isCustomRenderer() {
			return originalModel.isCustomRenderer();
		}

		@Nonnull
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, @Nonnull RandomSource rand) {
			return face == null ? genQuads : faceQuads.get(face);
		}

		@Override
		public BakedModel applyTransform(@Nonnull ItemTransforms.TransformType cameraTransformType, PoseStack stack, boolean leftHand) {
			super.applyTransform(cameraTransformType, stack, leftHand);
			return this;
		}
	}
}