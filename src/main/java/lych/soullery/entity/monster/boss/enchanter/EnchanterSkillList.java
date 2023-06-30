package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.Soullery;
import lych.soullery.util.CollectionUtils;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.world.ItemDestroyer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class EnchanterSkillList {
    private static final TranslationTextComponent VANISHING_TEXT = new TranslationTextComponent(Soullery.prefixMsg("enchanter", "vanishing_skill"));
    private static final TranslationTextComponent ADDITIONAL_EAS_TEXT = new TranslationTextComponent(Soullery.prefixMsg("enchanter", "additional_eas_skill"));
    private static final TranslationTextComponent EMPOWERMENT_TEXT = new TranslationTextComponent(Soullery.prefixMsg("enchanter", "empowerment_skill"));
    private static final TranslationTextComponent REGENERATION_TEXT = new TranslationTextComponent(Soullery.prefixMsg("enchanter", "regeneration_skill"));
    private static final TranslationTextComponent TRANSFORMATION_TEXT = new TranslationTextComponent(Soullery.prefixMsg("enchanter", "transformation_skill"));
    public static final EnchanterSkill VANISHING = new EnchanterSkill() {
        @Override
        public boolean canUse(EnchanterEntity enchanter, LivingEntity target) {
            return target instanceof ServerPlayerEntity;
        }

        @Override
        public void performSkill(EnchanterEntity enchanter, LivingEntity target) {
            ServerPlayerEntity player = (ServerPlayerEntity) target;
            Arrays.stream(enchanter.getVanishingSlotIndex(player)).forEach(i -> ItemDestroyer.tryVanish(player, i, 80));
            particlesAround(enchanter, 0x009865);
        }

        @Override
        public ITextComponent getSkillText(LivingEntity target) {
            return VANISHING_TEXT;
        }

        @Override
        public SoundEvent getSound() {
            return ModSoundEvents.ENCHANTER_CURSING_ITEM.get();
        }
    };
    public static final EnchanterSkill ADDITIONAL_EAS = new EnchanterSkill() {
        @Override
        public boolean canUse(EnchanterEntity enchanter, LivingEntity target) {
            return enchanter.countEAS() <= 12;
        }

        @Override
        public void performSkill(EnchanterEntity enchanter, LivingEntity target) {
            enchanter.summonEASNearby(3, 8);
            particlesAround(enchanter, 0x9f844d);
        }

        @Override
        public ITextComponent getSkillText(LivingEntity target) {
            return ADDITIONAL_EAS_TEXT;
        }

        @Override
        public SoundEvent getSound() {
            return ModSoundEvents.ENCHANTER_SUMMON.get();
        }
    };
    public static final EnchanterSkill EMPOWERMENT = new EnchanterSkill() {
        @Override
        public boolean canUse(EnchanterEntity enchanter, LivingEntity target) {
            return enchanter.getEASInsideArena().stream().filter(EnchantedArmorStandEntity::isMelee).count() >= 5;
        }

        @Override
        public void performSkill(EnchanterEntity enchanter, LivingEntity target) {
            for (EnchantedArmorStandEntity eas : enchanter.getEASInsideArena()) {
                if (eas.isMelee()) {
                    eas.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 20 * 120, 0));
                    particlesAround(enchanter, 0x932423);
                }
            }
        }

        @Override
        public ITextComponent getSkillText(LivingEntity target) {
            return new TranslationTextComponent(EMPOWERMENT_TEXT.getKey(), target.getDisplayName());
        }

        @Override
        public SoundEvent getSound() {
            return ModSoundEvents.ENCHANTER_EMPOWER.get();
        }
    };
    public static final EnchanterSkill REGENERATION = new EnchanterSkill() {
        @Override
        public boolean canUse(EnchanterEntity enchanter, LivingEntity target) {
            return enchanter.getHealth() <= enchanter.getMaxHealth() * 0.5;
        }

        @Override
        public void performSkill(EnchanterEntity enchanter, LivingEntity target) {
            enchanter.heal(enchanter.getMaxHealth() / 5);
            target.hurt(DamageSource.mobAttack(enchanter).bypassArmor(), 4);
            particlesAround(enchanter, 0xCD5CAB);
        }

        @Override
        public ITextComponent getSkillText(LivingEntity target) {
            return REGENERATION_TEXT;
        }

        @Override
        public SoundEvent getSound() {
            return ModSoundEvents.ENCHANTER_REGENERATE.get();
        }
    };
    public static final EnchanterSkill TRANSFORMATION = new EnchanterSkill() {
        @Override
        public boolean canUse(EnchanterEntity enchanter, LivingEntity target) {
            return target instanceof ServerPlayerEntity && InventoryUtils.anyMatch(((ServerPlayerEntity) target).inventory, this::isValidMeat);
        }

        @Override
        public void performSkill(EnchanterEntity enchanter, LivingEntity target) {
            List<ItemStack> inventoryList = InventoryUtils.listView(((ServerPlayerEntity) target).inventory);
            int countSum = 0;
            for (int i = 0; i < inventoryList.size(); i++) {
                if (isValidMeat(inventoryList.get(i))) {
                    int count = inventoryList.get(i).getCount();
                    inventoryList.set(i, new ItemStack(Items.ROTTEN_FLESH, count));
                    countSum += count;
                    if (countSum > 16) {
                        break;
                    }
                }
            }
            particlesAround(enchanter, 0xB44420);
        }

        @Override
        public ITextComponent getSkillText(LivingEntity target) {
            return TRANSFORMATION_TEXT;
        }

        @Override
        public SoundEvent getSound() {
            return ModSoundEvents.ENCHANTER_TRANSFORM.get();
        }

        private boolean isValidMeat(ItemStack stack) {
            if (stack.getItem() == Items.ROTTEN_FLESH) {
                return false;
            }
            return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
        }
    };

    private final List<EnchanterSkill> skills = new ArrayList<>();
    private final EnchanterEntity enchanter;

    private EnchanterSkillList(EnchanterEntity enchanter) {
        this.enchanter = enchanter;
    }

    public static EnchanterSkillList create(EnchanterEntity enchanter) {
        return init(new EnchanterSkillList(enchanter));
    }

    public static EnchanterSkillList init(EnchanterSkillList list) {
        list.add(VANISHING);
        list.add(ADDITIONAL_EAS);
        list.add(EMPOWERMENT);
        list.add(REGENERATION);
        list.add(TRANSFORMATION);
        return list;
    }

    public void add(EnchanterSkill skill) {
        skills.add(skill);
    }

    public List<EnchanterSkill> getUsableSkills(LivingEntity target) {
        return skills.stream().filter(skill -> skill.canUse(enchanter, target)).collect(Collectors.toList());
    }

    @Nullable
    public EnchanterSkill findRandomSkillOrNull(LivingEntity target) {
        List<EnchanterSkill> skills = getUsableSkills(target);
        if (skills.isEmpty()) {
            return null;
        }
        return CollectionUtils.getRandom(skills, enchanter.getRandom());
    }
}
