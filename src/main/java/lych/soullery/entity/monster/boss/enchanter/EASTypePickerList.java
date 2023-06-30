package lych.soullery.entity.monster.boss.enchanter;

import com.google.common.collect.Iterables;
import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.util.WeightedRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EASTypePickerList {
    private final List<EASTypePicker> pickers = new ArrayList<>();
    private final EnchanterEntity enchanter;

    private EASTypePickerList(EnchanterEntity enchanter) {
        this.enchanter = enchanter;
    }

    public static EASTypePickerList create(EnchanterEntity enchanter) {
        return EASTypePickers.init(new EASTypePickerList(enchanter));
    }

    public void add(EASTypePicker picker) {
        pickers.add(picker);
    }

    public void selfCheck() {
        boolean[] vis = new boolean[EASTypes.size()];
        for (EASTypePicker picker : pickers) {
            vis[picker.getType().getId()] = true;
        }
        List<ResourceLocation> invalid = new ArrayList<>();
        for (int i = 0; i < vis.length; i++) {
            boolean v = vis[i];
            if (!v) {
                EASType type = Objects.requireNonNull(EASTypes.byId(i));
                if (ConfigHelper.shouldFailhard()) {
                    invalid.add(type.getName());
                } else {
                    Soullery.LOGGER.error(type.getName() + " does not have a TypePicker");
                }
            }
        }
        if (!invalid.isEmpty()) {
            if (invalid.size() == 1) {
                throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + Iterables.getOnlyElement(invalid) + " does not have a TypePicker");
            } else {
                throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + invalid + " do not have a TypePicker");
            }
        }
    }

    public EASTypePicker findRandomTypePicker(LivingEntity target) {
        if (pickers.isEmpty()) {
            throw new IllegalStateException("EASTypePickerList is empty!");
        }
        pickers.forEach(picker -> picker.updateWeight(enchanter, target));
        return WeightedRandom.getRandomItem(enchanter.getRandom(), pickers);
    }
}
