package com.group_finity.mascot.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/** Pure bounded utility adjustment layered over Shimeji frequency weights. */
public final class NaturalBehaviorSelector {
    public record Candidate<T>(T value, String name, int frequency, int cooldown,
                               double energy, double boredom, double curiosity,
                               double affection, double social, double cursorNear, double companionNear,
                               double relationship) {}

    public <T> T select(List<Candidate<T>> input, NaturalBehaviorState state,
                        boolean cursorNear, boolean companionNear, RandomGenerator random) {
        if (input.isEmpty()) return null;
        List<Candidate<T>> eligible = input.stream().filter(c -> !state.isSuppressed(c.name())).toList();
        // Never starve or break restrictive legacy next-lists: fall back when all choices are suppressed.
        if (eligible.isEmpty()) eligible = input;
        List<Double> weights = new ArrayList<>(eligible.size());
        double total = 0;
        for (Candidate<T> c : eligible) {
            double utility = 1 + need(c.energy(), state, "energy") + need(c.boredom(), state, "boredom")
                    + need(c.curiosity(), state, "curiosity") + need(c.affection(), state, "affection")
                    + need(c.social(), state, "social") + (cursorNear ? c.cursorNear() : 0)
                    + (companionNear ? c.companionNear() : 0)
                    + c.relationship() * (state.getRelationship() - 50) / 50.0;
            double weight = Math.max(0.01, c.frequency() * Math.max(0.1, utility));
            weight /= 1 + state.recentCount(c.name());
            weights.add(weight);
            total += weight;
        }
        double draw = random.nextDouble(total);
        for (int i = 0; i < eligible.size(); i++) if ((draw -= weights.get(i)) < 0) {
            Candidate<T> selected = eligible.get(i);
            state.selected(selected.name(), selected.cooldown());
            return selected.value();
        }
        return eligible.getLast().value();
    }

    private double need(double coefficient, NaturalBehaviorState state, String need) {
        return coefficient * (state.getNeeds().get(need) - 50) / 50.0;
    }
}
