package lych.soullery.entity.ai.goal.wrapper;

import com.google.common.annotations.Beta;
import com.mojang.datafixers.util.Pair;
import lych.soullery.util.CollectionUtils;
import net.minecraft.entity.ai.goal.Goal;

import java.util.*;
import java.util.stream.Collectors;

@Beta
public class MultiGoal extends Goal {
    private final Goal[] goals;
    private final List<Pair<Goal, Boolean>> canUseList = new ArrayList<>();

    public MultiGoal(Goal... goals) {
        this.goals = goals;
        setFlags(EnumSet.copyOf(Arrays.stream(goals).map(Goal::getFlags).flatMap(Collection::stream).collect(Collectors.toSet())));
    }

    @Override
    public boolean canUse() {
        CollectionUtils.refill(canUseList, Arrays.stream(goals).map(goal -> Pair.of(goal, goal.canUse())).filter(Pair::getSecond).collect(Collectors.toList()));
        return !canUseList.isEmpty();
    }

    @Override
    public void start() {
        canUseList.stream().filter(Pair::getSecond).map(Pair::getFirst).forEach(Goal::start);
    }

    @Override
    public void tick() {
        canUseList.stream().map(Pair::getFirst).forEach(Goal::start);
    }

    @Override
    public boolean isInterruptable() {
        return Arrays.stream(goals).allMatch(Goal::isInterruptable);
    }

    @Override
    public boolean canContinueToUse() {
        List<Pair<Goal, Boolean>> removeList = canUseList.stream().filter(pair -> pair.getFirst().canContinueToUse()).collect(Collectors.toList());
        canUseList.removeAll(removeList);
        removeList.forEach(pair -> pair.getFirst().stop());
        return !canUseList.isEmpty();
    }

    @Override
    public void stop() {}
}
