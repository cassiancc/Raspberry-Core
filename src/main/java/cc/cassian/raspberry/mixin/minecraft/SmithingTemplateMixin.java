package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public abstract class SmithingTemplateMixin extends Item {
	@Shadow
	@Final
	private Component upgradeDescription;

	@Shadow
	@Final
	private static Component APPLIES_TO_TITLE;

	@Shadow
	@Final
	private static Component INGREDIENTS_TITLE;

	@Shadow
	@Final
	private Component appliesTo;

	@Shadow
	@Final
	private Component ingredients;

	public SmithingTemplateMixin(Properties properties) {
		super(properties);
	}

	@WrapMethod(method = "getDescriptionId")
	private String betterName(Operation<String> original) {
		if (ModConfig.get().noTemplates) {
			return ((TranslatableContents) this.upgradeDescription.getContents()).getKey();
		}else {
			return original.call();
		}
	}

	@WrapMethod(method = "appendHoverText")
	private <E> void removeWarning(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced, Operation<Void> original) {
		if (ModConfig.get().noTemplates) {
			super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
			tooltipComponents.add(APPLIES_TO_TITLE);
			tooltipComponents.add(CommonComponents.space().append(this.appliesTo));
			tooltipComponents.add(INGREDIENTS_TITLE);
			tooltipComponents.add(CommonComponents.space().append(this.ingredients));
		}

	}
}
