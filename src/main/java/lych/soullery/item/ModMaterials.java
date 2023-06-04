package lych.soullery.item;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public final class ModMaterials {
    public static final Material SOUL_LAVA = new MaterialBuilder(MaterialColor.COLOR_LIGHT_BLUE).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();

    private ModMaterials() {}

    public static class Tool implements IItemTier {
        public static final Tool REFINED_SOUL_METAL = new Builder().harvestLevel(3).maxUses(1451).speed(10).damageBonus(4).enchantmentValue(15).repairMaterial(ModItems.REFINED_SOUL_METAL_INGOT).build();
        public static final Tool SOUL_EXTRACTOR = new Builder().harvestLevel(0).maxUses(303).speed(3).damageBonus(1).enchantmentValue(10).repairMaterial(ModItems.SOUL_PIECE).build();

        private final int harvestLevel;
        private final int maxUses;
        private final float speed;
        private final float damageBonus;
        private final int enchantmentValue;
        private final Supplier<Ingredient> repairMaterial;

        private Tool(int harvestLevel, int maxUses, float speed, float damageBonus, int enchantmentValue, Supplier<Ingredient> repairMaterial) {
            this.harvestLevel = harvestLevel;
            this.maxUses = maxUses;
            this.speed = speed;
            this.damageBonus = damageBonus;
            this.enchantmentValue = enchantmentValue;
            this.repairMaterial = repairMaterial;
        }

        @Override
        public int getUses() {
            return maxUses;
        }

        @Override
        public float getSpeed() {
            return speed;
        }

        @Override
        public float getAttackDamageBonus() {
            return damageBonus;
        }

        @Override
        public int getLevel() {
            return harvestLevel;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairMaterial.get();
        }

        protected static class Builder {
            private int harvestLevel = -1;
            private int maxUses = -1;
            private float speed = -1;
            private float damageBonus = -1;
            private int enchantmentValue = 10;
            private Supplier<Ingredient> repairMaterial;

            protected Builder() {}

            protected Builder harvestLevel(int harvestLevel) {
                this.harvestLevel = harvestLevel;
                return this;
            }

            protected Builder maxUses(int maxUses) {
                this.maxUses = maxUses;
                return this;
            }

            protected Builder speed(float speed) {
                this.speed = speed;
                return this;
            }

            protected Builder damageBonus(float damageBonus) {
                this.damageBonus = damageBonus;
                return this;
            }

            protected Builder enchantmentValue(int enchantmentValue) {
                this.enchantmentValue = enchantmentValue;
                return this;
            }

            protected Builder repairMaterial(Item repairMaterial) {
                return repairMaterial(() -> Ingredient.of(repairMaterial));
            }

            protected Builder repairMaterial(Supplier<Ingredient> repairMaterial) {
                this.repairMaterial = repairMaterial;
                return this;
            }

            protected Tool build() {
                Tool tool = new Tool(harvestLevel, maxUses, speed, damageBonus, enchantmentValue, repairMaterial);
                checkArgument(tool.harvestLevel >= 0, "Invalid harvestLevel: " + tool.harvestLevel);
                checkArgument(tool.maxUses > 0, "Invalid maxUses: " + tool.maxUses);
                checkArgument(tool.speed > 0, "Invalid speed: " + tool.speed);
                checkArgument(tool.damageBonus >= 0, "Invalid damageBonus" + tool.damageBonus);
                checkArgument(tool.enchantmentValue >= 0, "Invalid enchantmentValue" + tool.enchantmentValue);
                requireNonNull(tool.repairMaterial, "Repair material supplier should be non-null");
                requireNonNull(tool.repairMaterial.get(), "Repair material should be non-null");
                checkArgument(!tool.repairMaterial.get().isEmpty(), "Repair material should not be empty");
                return tool;
            }
        }
    }

    public static class Armor implements IArmorMaterial {
        public static final Armor REFINED_SOUL_METAL = new Builder().name("refined_soul_metal").maxDamageFactor(30).defenseForSlots(4, 7, 10, 4).enchantmentValue(5).equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND).toughness(3).knockbackResistance(0.05f).repairMaterial(ModItems.REFINED_SOUL_METAL_INGOT).build();

        private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
        private final String name;
        private final int maxDamageFactor;
        private final int[] defenseArray;
        private final int enchantmentValue;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final LazyValue<Ingredient> repairMaterial;

        private Armor(String name, int maxDamageFactor, int[] defenseArray, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial) {
            this.name = name;
            this.maxDamageFactor = maxDamageFactor;
            this.defenseArray = defenseArray;
            this.enchantmentValue = enchantmentValue;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairMaterial = new LazyValue<>(repairMaterial);
        }

        @Override
        public int getDurabilityForSlot(EquipmentSlotType slot) {
            return HEALTH_PER_SLOT[slot.getIndex()] * maxDamageFactor;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slot) {
            return defenseArray[slot.getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return enchantmentValue;
        }

        @Override
        public SoundEvent getEquipSound() {
            return equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return repairMaterial.get();
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public String getName() {
            return name;
        }

        @Override
        public float getToughness() {
            return toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return knockbackResistance;
        }

        protected static class Builder {
            private String name;
            private int maxDamageFactor = -1;
            private int[] defenseArray = new int[]{-1, -1, -1, -1};
            private int enchantmentValue = -1;
            private SoundEvent equipSound;
            private float toughness = -1;
            private float knockbackResistance = -1;
            private Supplier<Ingredient> repairMaterial;

            protected Builder() {}

            protected Builder name(String name) {
                this.name = name;
                return this;
            }

            protected Builder maxDamageFactor(int maxDamageFactor) {
                this.maxDamageFactor = maxDamageFactor;
                return this;
            }

            protected Builder defenseForSlots(int boot, int leggings, int chestplate, int helmet) {
                return defenseForSlots(new int[]{boot, leggings, chestplate, helmet});
            }

            protected Builder defenseForSlots(int[] defenseArray) {
                this.defenseArray = defenseArray;
                return this;
            }

            protected Builder enchantmentValue(int enchantmentValue) {
                this.enchantmentValue = enchantmentValue;
                return this;
            }

            protected Builder equipSound(SoundEvent equipSound) {
                this.equipSound = equipSound;
                return this;
            }

            protected Builder toughness(float toughness) {
                this.toughness = toughness;
                return this;
            }

            protected Builder knockbackResistance(float knockbackResistance) {
                this.knockbackResistance = knockbackResistance;
                return this;
            }

            protected Builder repairMaterial(Item repairMaterial) {
                return repairMaterial(() -> Ingredient.of(repairMaterial));
            }

            protected Builder repairMaterial(Supplier<Ingredient> repairMaterial) {
                this.repairMaterial = repairMaterial;
                return this;
            }

            protected Armor build() {
                Armor armor = new Armor(name, maxDamageFactor, defenseArray, enchantmentValue, equipSound, toughness, knockbackResistance, repairMaterial);
                requireNonNull(armor.name, "Name should be non-null");
                checkArgument(armor.maxDamageFactor > 0, "Invalid maxDamageFactor: " + armor.maxDamageFactor);
                requireNonNull(armor.defenseArray, "DefenceArray should be non-null");
                checkArgument(armor.defenseArray.length == 4, "DefenceArray's length should be 4, not " + armor.defenseArray.length);
                checkArgument(Arrays.stream(armor.defenseArray).allMatch(i -> i >= 0), "DefenceArray's elements should not be negative");
                checkArgument(armor.enchantmentValue >= 0, "Invalid enchantmentValue: " + armor.enchantmentValue);
                requireNonNull(armor.equipSound, "EquipSound should be non-null");
                checkArgument(armor.toughness >= 0, "Invalid toughness: " + armor.toughness);
                checkArgument(armor.knockbackResistance >= 0, "Invalid knockbackResistance" + armor.knockbackResistance);
                requireNonNull(armor.repairMaterial, "Repair material supplier should be non-null");
                requireNonNull(armor.repairMaterial.get(), "Repair material should be non-null");
                return armor;
            }
        }
    }

    /**
     * An all-public version of {@link Material.Builder}
     */
    public static class MaterialBuilder {
        private PushReaction pushReaction = PushReaction.NORMAL;
        private boolean blocksMotion = true;
        private boolean flammable;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid = true;
        private final MaterialColor color;
        private boolean solidBlocking = true;

        public MaterialBuilder(MaterialColor p_i48270_1_) {
            this.color = p_i48270_1_;
        }

        public MaterialBuilder liquid() {
            this.liquid = true;
            return this;
        }

        public MaterialBuilder nonSolid() {
            this.solid = false;
            return this;
        }

        public MaterialBuilder noCollider() {
            this.blocksMotion = false;
            return this;
        }

        public MaterialBuilder notSolidBlocking() {
            this.solidBlocking = false;
            return this;
        }

        public MaterialBuilder flammable() {
            this.flammable = true;
            return this;
        }

        public MaterialBuilder replaceable() {
            this.replaceable = true;
            return this;
        }

        public MaterialBuilder destroyOnPush() {
            this.pushReaction = PushReaction.DESTROY;
            return this;
        }

        public MaterialBuilder notPushable() {
            this.pushReaction = PushReaction.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(color, liquid, solid, blocksMotion, solidBlocking, flammable, replaceable, pushReaction);
        }
    }
}
