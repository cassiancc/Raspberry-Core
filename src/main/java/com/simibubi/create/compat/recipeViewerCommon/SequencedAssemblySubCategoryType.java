package com.simibubi.create.compat.recipeViewerCommon;

import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;

import java.util.function.Supplier;

public record SequencedAssemblySubCategoryType(Supplier<Supplier<EmiSequencedAssemblySubCategory>> emi) {

	public static final SequencedAssemblySubCategoryType PRESSING = new SequencedAssemblySubCategoryType(
			() -> EmiSequencedAssemblySubCategory.AssemblyPressing::new
	);
	public static final SequencedAssemblySubCategoryType SPOUTING = new SequencedAssemblySubCategoryType(
			() -> EmiSequencedAssemblySubCategory.AssemblySpouting::new
	);
	public static final SequencedAssemblySubCategoryType DEPLOYING = new SequencedAssemblySubCategoryType(
			() -> EmiSequencedAssemblySubCategory.AssemblyDeploying::new
	);
	public static final SequencedAssemblySubCategoryType CUTTING = new SequencedAssemblySubCategoryType(
			() -> EmiSequencedAssemblySubCategory.AssemblyCutting::new
	);
}
