package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.LazyValue;

import static lych.soullery.util.WeightedRandom.makeItem;

public class EASTypePickers {
    public static EASTypePickerList init(EASTypePickerList list) {
        list.add(new ToughTypePicker(EASTypes.OBSIDIAN, 100));
        list.add(new ToughTypePicker(EASTypes.MAGMA, 100));
        list.add(new ToughTypePicker(EASTypes.WOOD, 75));
        list.add(new ToughTypePicker(EASTypes.CACTUS, 50));
        list.add(new SlimePicker(EASTypes.SLIME, 50));
        list.add(new ControllableTypePicker(EASTypes.TNT, 50));
        list.add(new EffectTypePicker(EASTypes.ICE, Utils.FROSTED, 100));
        list.add(new EffectTypePicker(EASTypes.MYCELIUM, Effects.POISON, 75));
        list.add(new EffectTypePicker(EASTypes.MUSHROOM, Effects.HUNGER, 75));
        list.add(new PistonPicker(EASTypes.PISTON, 75));
        list.add(new DispenserPicker(EASTypes.DISPENSER, 100));
        return list;
    }

    public static class ToughTypePicker extends EASTypePicker {
        public ToughTypePicker(EASType type, int baseWeight) {
            super(type, baseWeight, EASPlacements.CENTER);
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            double mul = 1;
            mul += (1 - enchanter.getHealth() / enchanter.getMaxHealth());
            return mul;
        }
    }

    public static class ControllableTypePicker extends EASTypePicker {
        public ControllableTypePicker(EASType type, int baseWeight) {
            super(type, baseWeight, makeItem(EASPlacements.MIDPOINT, 10), makeItem(EASPlacements.TARGET, 15));
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            double mul = 1;
            mul += Math.max(0.5, enchanter.distanceTo(target) * 0.02);
            mul += Math.max(1, target.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.02);
            return mul;
        }
    }

    public static class SlimePicker extends EASTypePicker {
        public SlimePicker(EASType type, int baseWeight) {
            super(type, baseWeight, makeItem(EASPlacements.MIDPOINT, 10), makeItem(EASPlacements.TARGET, 15));
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            double mul = 1;
            if (target.getMainHandItem().isEmpty()) {
                if (!(target instanceof PlayerEntity)) {
                    return 0;
                } else {
                    mul = 0.4;
                }
            }
            if (target.getMainHandItem().getItem() instanceof SwordItem) {
                mul += Math.max(((SwordItem) target.getMainHandItem().getItem()).getDamage() * 0.07, 1);
            }
            if (target.getMainHandItem().getItem() instanceof BowItem) {
                mul += 0.5;
            }
            return mul;
        }
    }

    public static class PistonPicker extends EASTypePicker {

        public PistonPicker(EASType type, int baseWeight) {
            super(type, baseWeight, makeItem(EASPlacements.CENTER, 10), makeItem(EASPlacements.MIDPOINT, 15), makeItem(EASPlacements.TARGET, 15));
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            double mul = 3;
            mul -= Math.max(2.5, enchanter.distanceTo(target) * 0.1);
            mul += Math.max(0.5, target.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.05);
            return mul;
        }
    }

    public static class DispenserPicker extends EASTypePicker {

        public DispenserPicker(EASType type, int baseWeight) {
            super(type, baseWeight, makeItem(EASPlacements.CENTER, 10), makeItem(EASPlacements.MIDPOINT, 4));
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            double mul = 1;
            if (target.getMainHandItem().getItem() instanceof ShootableItem) {
                mul += 1;
            }
            return mul;
        }
    }

    public static class EffectTypePicker extends EASTypePicker {
        private final LazyValue<Effect> effect;

        public EffectTypePicker(EASType type, Effect effect, int baseWeight) {
            this(type, new LazyValue<>(() -> effect), baseWeight);
        }

        public EffectTypePicker(EASType type, LazyValue<Effect> effect, int baseWeight) {
            super(type, baseWeight, EASPlacements.TARGET);
            this.effect = effect;
        }

        @Override
        protected double getWeightMultiplier(EnchanterEntity enchanter, LivingEntity target) {
            if (target.hasEffect(effect.get())) {
                return 0;
            }
            return 1;
        }
    }
}
