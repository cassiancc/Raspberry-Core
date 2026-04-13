package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.blocks.MoltenLiquidBlock;
import cc.cassian.raspberry.fluids.MoltenCauldronBlock;
import cc.cassian.raspberry.fluids.MoltenFlowingFluid;
import cc.cassian.raspberry.fluids.MoltenFluidExtensions;
import cc.cassian.raspberry.items.MoltenBucketItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID);
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MOD_ID);

	public static final RegistryObject<FluidType> MOLTEN_BRASS_TYPE = registerFluidType("molten_brass");
	public static final RegistryObject<FlowingFluid> MOLTEN_BRASS = FLUIDS.register("molten_brass", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_BRASS_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_BRASS = FLUIDS.register("flowing_molten_brass", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_BRASS_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_BRASS_PROPERTIES = registerProperties("molten_brass", MOLTEN_BRASS_TYPE, MOLTEN_BRASS, FLOWING_MOLTEN_BRASS);

	public static final RegistryObject<FluidType> MOLTEN_BRONZE_TYPE = registerFluidType("molten_bronze");
	public static final RegistryObject<FlowingFluid> MOLTEN_BRONZE = FLUIDS.register("molten_bronze", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_BRONZE_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_BRONZE = FLUIDS.register("flowing_molten_bronze", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_BRONZE_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_BRONZE_PROPERTIES = registerProperties("molten_bronze", MOLTEN_BRONZE_TYPE, MOLTEN_BRONZE, FLOWING_MOLTEN_BRONZE);

	public static final RegistryObject<FluidType> MOLTEN_COPPER_TYPE = registerFluidType("molten_copper");
	public static final RegistryObject<FlowingFluid> MOLTEN_COPPER = FLUIDS.register("molten_copper", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_COPPER_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_COPPER = FLUIDS.register("flowing_molten_copper", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_COPPER_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_COPPER_PROPERTIES = registerProperties("molten_copper", MOLTEN_COPPER_TYPE, MOLTEN_COPPER, FLOWING_MOLTEN_COPPER);

	public static final RegistryObject<FluidType> MOLTEN_ELECTRUM_TYPE = registerFluidType("molten_electrum");
	public static final RegistryObject<FlowingFluid> MOLTEN_ELECTRUM = FLUIDS.register("molten_electrum", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_ELECTRUM_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ELECTRUM = FLUIDS.register("flowing_molten_electrum", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_ELECTRUM_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_ELECTRUM_PROPERTIES = registerProperties("molten_electrum", MOLTEN_ELECTRUM_TYPE, MOLTEN_ELECTRUM, FLOWING_MOLTEN_ELECTRUM);

	public static final RegistryObject<FluidType> MOLTEN_GOLD_TYPE = registerFluidType("molten_gold");
	public static final RegistryObject<FlowingFluid> MOLTEN_GOLD = FLUIDS.register("molten_gold", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_GOLD_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_GOLD = FLUIDS.register("flowing_molten_gold", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_GOLD_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_GOLD_PROPERTIES = registerProperties("molten_gold", MOLTEN_GOLD_TYPE, MOLTEN_GOLD, FLOWING_MOLTEN_GOLD);

	public static final RegistryObject<FluidType> MOLTEN_IRON_TYPE = registerFluidType("molten_iron");
	public static final RegistryObject<FlowingFluid> MOLTEN_IRON = FLUIDS.register("molten_iron", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_IRON_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_IRON = FLUIDS.register("flowing_molten_iron", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_IRON_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_IRON_PROPERTIES = registerProperties("molten_iron", MOLTEN_IRON_TYPE, MOLTEN_IRON, FLOWING_MOLTEN_IRON);

	public static final RegistryObject<FluidType> MOLTEN_LEAD_TYPE = registerFluidType("molten_lead");
	public static final RegistryObject<FlowingFluid> MOLTEN_LEAD = FLUIDS.register("molten_lead", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_LEAD_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_LEAD = FLUIDS.register("flowing_molten_lead", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_LEAD_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_LEAD_PROPERTIES = registerProperties("molten_lead", MOLTEN_LEAD_TYPE, MOLTEN_LEAD, FLOWING_MOLTEN_LEAD);

	public static final RegistryObject<FluidType> MOLTEN_NECROMIUM_TYPE = registerFluidType("molten_necromium");
	public static final RegistryObject<FlowingFluid> MOLTEN_NECROMIUM = FLUIDS.register("molten_necromium", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_NECROMIUM_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_NECROMIUM = FLUIDS.register("flowing_molten_necromium", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_NECROMIUM_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_NECROMIUM_PROPERTIES = registerProperties("molten_necromium", MOLTEN_NECROMIUM_TYPE, MOLTEN_NECROMIUM, FLOWING_MOLTEN_NECROMIUM);

	public static final RegistryObject<FluidType> MOLTEN_NETHERITE_TYPE = registerFluidType("molten_netherite");
	public static final RegistryObject<FlowingFluid> MOLTEN_NETHERITE = FLUIDS.register("molten_netherite", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_NETHERITE_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_NETHERITE = FLUIDS.register("flowing_molten_netherite", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_NETHERITE_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_NETHERITE_PROPERTIES = registerProperties("molten_netherite", MOLTEN_NETHERITE_TYPE, MOLTEN_NETHERITE, FLOWING_MOLTEN_NETHERITE);

	public static final RegistryObject<FluidType> MOLTEN_ROSE_GOLD_TYPE = registerFluidType("molten_rose_gold");
	public static final RegistryObject<FlowingFluid> MOLTEN_ROSE_GOLD = FLUIDS.register("molten_rose_gold", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_ROSE_GOLD_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ROSE_GOLD = FLUIDS.register("flowing_molten_rose_gold", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_ROSE_GOLD_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_ROSE_GOLD_PROPERTIES = registerProperties("molten_rose_gold", MOLTEN_ROSE_GOLD_TYPE, MOLTEN_ROSE_GOLD, FLOWING_MOLTEN_ROSE_GOLD);

	public static final RegistryObject<FluidType> MOLTEN_SILVER_TYPE = registerFluidType("molten_silver");
	public static final RegistryObject<FlowingFluid> MOLTEN_SILVER = FLUIDS.register("molten_silver", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_SILVER_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_SILVER = FLUIDS.register("flowing_molten_silver", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_SILVER_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_SILVER_PROPERTIES = registerProperties("molten_silver", MOLTEN_SILVER_TYPE, MOLTEN_SILVER, FLOWING_MOLTEN_SILVER);

	public static final RegistryObject<FluidType> MOLTEN_STEEL_TYPE = registerFluidType("molten_steel");
	public static final RegistryObject<FlowingFluid> MOLTEN_STEEL = FLUIDS.register("molten_steel", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_STEEL_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_STEEL = FLUIDS.register("flowing_molten_steel", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_STEEL_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_STEEL_PROPERTIES = registerProperties("molten_steel", MOLTEN_STEEL_TYPE, MOLTEN_STEEL, FLOWING_MOLTEN_STEEL);

	public static final RegistryObject<FluidType> MOLTEN_ZINC_TYPE = registerFluidType("molten_zinc");
	public static final RegistryObject<FlowingFluid> MOLTEN_ZINC = FLUIDS.register("molten_zinc", () -> new MoltenFlowingFluid.Source(RaspberryFluids.MOLTEN_ZINC_PROPERTIES));
	public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ZINC = FLUIDS.register("flowing_molten_zinc", () -> new MoltenFlowingFluid.Flowing(RaspberryFluids.MOLTEN_ZINC_PROPERTIES));
	public static final ForgeFlowingFluid.Properties MOLTEN_ZINC_PROPERTIES = registerProperties("molten_zinc", MOLTEN_ZINC_TYPE, MOLTEN_ZINC, FLOWING_MOLTEN_ZINC);



	private static ForgeFlowingFluid.Properties registerProperties(String name, RegistryObject<FluidType> moltenFluidType, RegistryObject<FlowingFluid> source, RegistryObject<FlowingFluid> flowing) {
		Supplier<? extends Item> bucket = bucket(name, source);
		RaspberryBlocks.registerBlock("%s_cauldron".formatted(name), ()-> new MoltenCauldronBlock(bucket, BlockBehaviour.Properties.copy(Blocks.LAVA_CAULDRON)));
		return new ForgeFlowingFluid.Properties(moltenFluidType, source, flowing).bucket(bucket).block(block(name, source));
	}

	private static Supplier<? extends LiquidBlock> block(String name, RegistryObject<FlowingFluid> fluid) {
		return RaspberryBlocks.BLOCKS.register(name, ()->new MoltenLiquidBlock(fluid, BlockBehaviour.Properties.of(Material.LAVA).lightLevel(state->15).noLootTable().noCollission().randomTicks().strength(100f)));
	}

	private static Supplier<? extends Item> bucket(String name, RegistryObject<FlowingFluid> moltenLead) {
		return RaspberryItems.registerItem("%s_bucket".formatted(name), ()->new MoltenBucketItem(name, moltenLead, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MATERIALS)));
	}

	static RegistryObject<FluidType> registerFluidType(String name) {
		return FLUID_TYPES.register(name, () -> new FluidType(getProperties(name)) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(new MoltenFluidExtensions(name));
			}
		});
	}

	private static FluidType.Properties getProperties(String name) {
		return FluidType.Properties.create()
				.descriptionId("block.raspberry."+name)
				.motionScale(0)
				.canExtinguish(false)
				.supportsBoating(false)
				.lightLevel(8)
				.density(2000)
				.temperature(630)
				.viscosity(10000)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA);
	}


}
