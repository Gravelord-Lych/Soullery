package lych.soullery.data.advancement;

import lych.soullery.Soullery;
import lych.soullery.advancements.criterion.*;
import lych.soullery.entity.ModEntities;
import lych.soullery.item.ModItems;
import lych.soullery.item.SoulPieceItem;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

import static net.minecraft.advancements.Advancement.Builder.advancement;

public class SoulLandAdvancement implements AdvancementRegisterer {
    private static final String CATEGORY = "soul_land";

    @Override
    public void register(Consumer<Advancement> registry, ExistingFileHelper helper) {
        Advancement soulCollector = advancement()
                .display(getSoulPieceIcon(), title("soul_collector"), description("soul_collector"), Soullery.advancementBackground(CATEGORY), FrameType.TASK, false, false, false)
                .addCriterion("collected_soul_piece", DroppedSoulPieceTrigger.create())
                .save(registry, name("root"), helper);
        Advancement soulCrafter = advancement()
                .parent(soulCollector)
                .display(getSoulContainerIcon(), title("soul_crafter"), description("soul_crafter"), null, FrameType.TASK, true, true, false)
                .addCriterion("crafted_soul_container", CraftedSoulContainerTrigger.create())
                .save(registry, name("soul_crafter"), helper);
        Advancement soulMaster = advancement()
                .parent(soulCollector)
                .display(ModItems.HORCRUX_CARRIER, title("soul_master"), description("soul_master"), null, FrameType.TASK, true, true, false)
                .addCriterion("used_horcrux_carrier", ConsumeItemTrigger.Instance.usedItem(ModItems.HORCRUX_CARRIER))
                .save(registry, name("soul_master"), helper);
        Advancement soulRealm = advancement()
                .parent(soulMaster)
                .display(Items.SOUL_SOIL, title("soul_realm"), description("soul_realm"), null, FrameType.TASK, true, true, false)
                .addCriterion("entered_soul_land", ChangeDimensionTrigger.Instance.changedDimensionTo(ModDimensions.SOUL_LAND))
                .save(registry, name("soul_realm"), helper);
        Advancement blazeHunter = advancement()
                .parent(soulRealm)
                .display(ModItems.ENERGIZED_BLAZE_ROD, title("blaze_hunter"), description("blaze_hunter"), null, FrameType.TASK, true, true, false)
                .addCriterion("killed_energized_blaze", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.ENERGIZED_BLAZE)))
                .save(registry, name("blaze_hunter"), helper);
        Advancement burningUp = advancement()
                .parent(blazeHunter)
                .display(getBurningPowderIcon(), title("burning_up"), description("burning_up"), null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("killed_energized_blaze_on_fire", BurningUpTrigger.create())
                .save(registry, name("burning_up"), helper);
        Advancement destroyer = advancement()
                .parent(blazeHunter)
                .display(Items.IRON_AXE, title("destroyer"), description("destroyer"), null, FrameType.CHALLENGE, true, true, false)
                .addCriterion("kill_more_than_150_eas", DestroyerTrigger.create())
                .save(registry, name("destroyer"), helper);
        Advancement destroyerII = advancement()
                .parent(destroyer)
                .display(ModItems.REFINED_SOUL_METAL_AXE, title("destroyer_l2"), description("destroyer_l2"), null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("explode_no_disassemblers", DestroyerIITrigger.create())
                .save(registry, name("destroyer_l2"), helper);
    }

    private static ItemStack getSoulPieceIcon() {
        ItemStack stack = new ItemStack(ModItems.SOUL_PIECE);
        SoulPieceItem.setType(stack, EntityType.PIG);
        return stack;
    }

    private static ItemStack getSoulContainerIcon() {
        ItemStack stack = new ItemStack(ModItems.SOUL_CONTAINER);
        SoulPieceItem.setType(stack, EntityType.PIG);
        return stack;
    }

    private static ItemStack getBurningPowderIcon() {
        ItemStack stack = new ItemStack(ModItems.ENERGIZED_BLAZE_POWDER);
        stack.enchant(Enchantments.FIRE_ASPECT, 2);
        return stack;
    }

    private static TranslationTextComponent title(String name) {
        return new TranslationTextComponent(Soullery.prefixAdvancementTitle(CATEGORY, name));
    }

    private static TranslationTextComponent description(String name) {
        return new TranslationTextComponent(Soullery.prefixAdvancementDescription(CATEGORY, name));
    }

    private static ResourceLocation name(String name) {
        return Soullery.prefix(CATEGORY + "/" + name);
    }
}
