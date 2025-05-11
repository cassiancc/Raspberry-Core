package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.RaspberryMod;
import cofh.ensorcellation.init.EnsorcEnchantments;
import com.brokenkeyboard.usefulspyglass.UsefulSpyglass;
import com.github.alexthe668.domesticationinnovation.server.enchantment.DIEnchantmentRegistry;
import com.github.alexthe668.domesticationinnovation.server.item.DIItemRegistry;
import com.simibubi.create.AllItems;
import com.teamabnormals.allurement.core.registry.AllurementEnchantments;
import de.cadentem.additional_enchantments.registry.AEEnchantments;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.miningmaster.init.MMEnchantments;
import vectorwing.farmersdelight.common.registry.ModEnchantments;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

import java.util.List;

public class EmiSmithingRecipe implements EmiRecipe {
    private final EmiIngredient input1;
    private final EmiStack input2;
    private final EmiStack output;
    private final ResourceLocation id;
    private final int uniq;

    public EmiSmithingRecipe(EmiIngredient input1, EmiStack input2, EmiStack output, ResourceLocation id) {
        this.uniq = EmiUtil.RANDOM.nextInt();
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.id = id;
    }

    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.SMITHING;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public List<EmiIngredient> getInputs() {
        return List.of(this.input1, this.input2);
    }

    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    public boolean supportsRecipeTree() {
        return false;
    }

    public int getDisplayWidth() {
        return 125;
    }

    public int getDisplayHeight() {
        return 18;
    }

    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addGeneratedSlot((r) -> this.input1, this.uniq, 0, 0);
        widgets.addSlot(this.input2, 49, 0);
        widgets.addGeneratedSlot((r) -> this.output, this.uniq, 107, 0).recipeContext(this);
    }

    public static void addEnchantments(EmiRegistry emiRegistry) {
        final var EVERLASTING = get("everlasting");
        final var AQUATIC = get("aquatic");
        final var BEASTLY = get("beastly");
        final var CYCLIC = get("cyclic");
        final var FLINGING = get("flinging");
        final var ENDURING = get("enduring");
        final var FROST = get("glacial");
        final var HALLOWED = get("hallowed");
        final var HAUNTED = get("haunted");
        final var HEAVY = get("heavy");
        final var INFESTED = get("infested");
        final var OTHERWORLDLY = get("otherworldly");
        final var PIERCING = get("piercing");
        final var PULLING = get("pulling");
        final var SILENT = get("silent");
        final var SWIFT = get("swift");
        final Enchantment GUARD_BREAK = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryBuild("kubejs", "guard_break"));

        // EVERLASTING - UNRBEAKING
        addRecipe(emiRegistry, // ARMOUR
                getArmour(), Tags.Items.ARMORS, Enchantments.UNBREAKING, EVERLASTING, "everlasting_armour");
        // EVERLASTING - UNBREAKING
        addRecipe(emiRegistry, // TOOLS
                getTools(), Tags.Items.TOOLS, Enchantments.UNBREAKING, EVERLASTING, "everlasting_tools");

        // AQUATIC - RESPIRATION
        addRecipe(emiRegistry, // HELMETS
                Items.IRON_HELMET, Items.IRON_HELMET,
                Enchantments.RESPIRATION, AQUATIC,
                "respiration_helmet");
        addRecipe(emiRegistry, // BACKTANKS
                AllItems.COPPER_BACKTANK.get(), AllItems.COPPER_BACKTANK.get(),
                Enchantments.RESPIRATION, AQUATIC,
                "respiration_backtank");
        // AQUATIC - DEPTH STRIDER
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.DEPTH_STRIDER, AQUATIC,
                "depth_strider_boots");
        // AQUATIC - AMPHIBIOUS
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.AMPHIBIOUS, AQUATIC,
                "amphibious");

        // BEASTLY - CAVALIER
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                EnsorcEnchantments.CAVALIER.get(), BEASTLY,
                "cavalier_sword");
        addRecipe(emiRegistry,
                Items.DIAMOND_AXE, Tags.Items.TOOLS_AXES,
                EnsorcEnchantments.CAVALIER.get(), BEASTLY,
                "cavalier_axe");
        // BEASTLY - MULTI-LEAP
        addRecipe(emiRegistry,
                getLeggings(), Tags.Items.ARMORS_LEGGINGS,
                MMEnchantments.KNIGHT_JUMP.get(), BEASTLY,
                "multi_leap");
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.INTIMIDATION, BEASTLY,
                "intimidation");

        // CYCLIC - SWEEPING EDGE
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.SWEEPING_EDGE, CYCLIC,
                "sweeping_edge");
        // CYCLIC - RIPTIDE
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.RIPTIDE, CYCLIC,
                "riptide");
        // CYCLIC - VENGEANCE
        addRecipe(emiRegistry,
                getArmour(), Tags.Items.ARMORS,
                AllurementEnchantments.VENGEANCE.get(), CYCLIC,
                "vengeance");

        // ENDURING - VITALITY
        addRecipe(emiRegistry,
                getArmour(), Tags.Items.ARMORS,
                EnsorcEnchantments.VITALITY.get(), ENDURING,
                "vitality");
        // ENDURING - HEALTH BOOST
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.HEALTH_BOOST, ENDURING,
                "health_boost");

        // FLINGING - LAUNCH
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                AllurementEnchantments.LAUNCH.get(), FLINGING,
                "launch_sword");
        // FLINGING - VOLLEY
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                EnsorcEnchantments.VOLLEY.get(), FLINGING,
                "volley_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                EnsorcEnchantments.VOLLEY.get(), FLINGING,
                "volley_crossbow");
        // FLINGING - DEFLECTION
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.DEFLECTION, FLINGING,
                "deflection");

        // FROST - FROST WALKER
        addRecipe(emiRegistry, // BOOTS
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.FROST_WALKER, FROST,
                "frost_walker_boots");
        addRecipe(emiRegistry, // HORSE ARMOUR
                Items.DIAMOND_HORSE_ARMOR, Items.DIAMOND_HORSE_ARMOR,
                Enchantments.FROST_WALKER, FROST,
                "frost_walker_horse_armour");
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.FROST_FANG, FROST,
                "frost_fang");

        // HALLOWED - SMITE
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.SMITE, HALLOWED,
                "smite_sword");
        addRecipe(emiRegistry,
                Items.DIAMOND_AXE, Tags.Items.TOOLS_AXES,
                Enchantments.SMITE, HALLOWED,
                "smite_axe");
        // HALLOWED - CHANNELING
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.CHANNELING, HALLOWED,
                "channeling");
        // HALLOWED - SILVER EYE
        addRecipe(emiRegistry,
                Items.SPYGLASS, Items.SPYGLASS,
                UsefulSpyglass.MARKING.get(), HALLOWED,
                "silver_eye");

        // HAUNTED - SOUL CHASER
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                AEEnchantments.HOMING.get(), HAUNTED,
                "soul_chaser_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                AEEnchantments.HOMING.get(), HAUNTED,
                "soul_chaser_crossbow");
        // HAUNTED - SOUL SPEED
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                Enchantments.SOUL_SPEED, HAUNTED,
                "soul_speed");
        // HAUNTED - TOTAL RECALL
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.TOTAL_RECALL, HAUNTED,
                "total_recall");

        // HEAVY - GUARD BREAK
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                GUARD_BREAK, HEAVY,
                "guard_break_sword");
        addRecipe(emiRegistry,
                Items.DIAMOND_AXE, Tags.Items.TOOLS_AXES,
                GUARD_BREAK, HEAVY,
                "guard_break_axe");
        // HEAVY - BRACEWALK
        addRecipe(emiRegistry,
                getLeggings(), Tags.Items.ARMORS_LEGGINGS,
                AEEnchantments.BRACEWALK.get(), HEAVY,
                "bracewalk");
        // HEAVY - SHOCKWAVE
        addRecipe(emiRegistry,
                getBoots(), Tags.Items.ARMORS_BOOTS,
                AllurementEnchantments.SHOCKWAVE.get(), HEAVY,
                "shockwave");

        // INFESTED - BANE OF ARTHROPODS
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                Enchantments.BANE_OF_ARTHROPODS, INFESTED,
                "bane_sword");
        addRecipe(emiRegistry,
                Items.DIAMOND_AXE, Tags.Items.TOOLS_AXES,
                Enchantments.BANE_OF_ARTHROPODS, INFESTED,
                "bane_axe");
        // INFESTED - SPREAD OF AILMENTS
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                AllurementEnchantments.SPREAD_OF_AILMENTS.get(), INFESTED,
                "spread_of_ailments");

        // OTHERWORLDLY - STASIS
        addRecipe(emiRegistry,
                Items.BOW, Items.BOW,
                ModRegistry.STASIS_ENCHANTMENT.get(), OTHERWORLDLY,
                "bracewalk_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Items.CROSSBOW,
                ModRegistry.STASIS_ENCHANTMENT.get(), OTHERWORLDLY,
                "bracewalk_crossbow");
        addRecipe(emiRegistry,
                ModRegistry.BUBBLE_BLOWER.get(), ModRegistry.BUBBLE_BLOWER.get(),
                ModRegistry.STASIS_ENCHANTMENT.get(), OTHERWORLDLY,
                "bracewalk_bubble");
        // OTHERWORLDLY - DISPLACEMENT
        addRecipe(emiRegistry,
                Items.DIAMOND_CHESTPLATE, Tags.Items.ARMORS_CHESTPLATES,
                EnsorcEnchantments.DISPLACEMENT.get(), OTHERWORLDLY,
                "displacement");
        // OTHERWORLDLY - TETHERED TELEPORT
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.TETHERED_TELEPORT, OTHERWORLDLY,
                "tethered_teleport");

        // PIERCING - TRUESHOT
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                EnsorcEnchantments.TRUESHOT.get(), PIERCING,
                "trueshot_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                EnsorcEnchantments.TRUESHOT.get(), PIERCING,
                "trueshot_crossbow");
        // PIERCING - IMPALING
        addRecipe(emiRegistry,
                Items.TRIDENT, Tags.Items.TOOLS_TRIDENTS,
                Enchantments.IMPALING, PIERCING,
                "impaling");

        // PULLING - REELING
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                AllurementEnchantments.REELING.get(), PULLING,
                "reeling");
        // PULLING - REACH
        addRecipe(emiRegistry,
                Items.DIAMOND_CHESTPLATE, Tags.Items.ARMORS_CHESTPLATES,
                EnsorcEnchantments.REACH.get(), PULLING,
                "reach");

        // SILENT - BACKSTABBING
        addRecipe(emiRegistry,
                ModItems.DIAMOND_KNIFE.get(), ForgeTags.TOOLS_KNIVES,
                ModEnchantments.BACKSTABBING.get(), SILENT,
                "backstabbing");
        // SILENT - SWIFT SNEAK
        addRecipe(emiRegistry,
                getLeggings(), Tags.Items.ARMORS_LEGGINGS,
                Enchantments.SWIFT_SNEAK, SILENT,
                "swift_sneak");
        // SILENT - MUFFLED
        addRecipe(emiRegistry,
                DIItemRegistry.COLLAR_TAG.get(), DIItemRegistry.COLLAR_TAG.get(),
                DIEnchantmentRegistry.MUFFLED, SILENT,
                "muffled");

        // SWIFT - SWIFTSTRIKE
        addRecipe(emiRegistry,
                getSword(), Tags.Items.TOOLS_SWORDS,
                AEEnchantments.FASTER_ATTACKS.get(), SWIFT,
                "swift_sword");
        addRecipe(emiRegistry,
                Items.DIAMOND_AXE, Tags.Items.TOOLS_AXES,
                AEEnchantments.FASTER_ATTACKS.get(), SWIFT,
                "swift_axe");
        // SWIFT - QUICK DRAW
        addRecipe(emiRegistry,
                Items.BOW, Tags.Items.TOOLS_BOWS,
                EnsorcEnchantments.QUICK_DRAW.get(), SWIFT,
                "swift_bow");
        addRecipe(emiRegistry,
                Items.CROSSBOW, Tags.Items.TOOLS_CROSSBOWS,
                EnsorcEnchantments.QUICK_DRAW.get(), SWIFT,
                "swift_crossbow");

    }

    private static Item getLeggings() {
        return Items.DIAMOND_LEGGINGS;
    }

    private static Item getBoots() {
        return Items.DIAMOND_BOOTS;
    }

    private static Item getSword() {
        return Items.DIAMOND_SWORD;
    }

    private static Item getArmour() {
        return Items.DIAMOND_CHESTPLATE;
    }

    private static Item getTools() {
        return Items.DIAMOND_PICKAXE;
    }

    public static void addRecipe(EmiRegistry emiRegistry, Item item, TagKey<Item> tag, Enchantment enchantment, Item tablet, String id) {
        var enchantedGear = new ItemStack(item);
        enchantedGear.enchant(enchantment, 1);
        emiRegistry.addRecipe(new EmiSmithingRecipe(
                EmiIngredient.of(tag),
                EmiStack.of(new ItemStack(tablet)),
                EmiStack.of(enchantedGear),
                RaspberryMod.locate("/smithing/"+id)
        ));
    }

    public static void addRecipe(EmiRegistry emiRegistry, Item item, Item tag, Enchantment enchantment, Item tablet, String id) {
        var enchantedGear = new ItemStack(item);
        enchantedGear.enchant(enchantment, 1);
        emiRegistry.addRecipe(new EmiSmithingRecipe(
                EmiIngredient.of(Ingredient.of(tag)),
                EmiStack.of(new ItemStack(tablet)),
                EmiStack.of(enchantedGear),
                RaspberryMod.locate("/smithing/"+id)
        ));
    }

    public static ResourceLocation tablet(String id) {
        return new ResourceLocation("kubejs", id+"_tablet");
    }

    public static Item get(String id) {
        return Registry.ITEM.get(tablet(id));
    }
}
