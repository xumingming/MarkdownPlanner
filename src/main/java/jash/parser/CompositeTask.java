package jash.parser;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

public class CompositeTask extends Task {
    private Map<String, Integer> owner2ManDays = new HashMap<>();
    private Map<String, Double> owner2FinishedCost = new HashMap<>();

    public CompositeTask(Header header, String name, LocalDate projectStartDate) {
        this.name = name;
        this.header = header;
        this.projectStartDate = projectStartDate;
    }

    @Override
    public String getOwner() {
        return Joiner.on("/").join(
            this.owner2ManDays.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );
    }

    public void addOwnerCost(String owner, int cost, double finishedCost) {
        if (!owner2ManDays.containsKey(owner)) {
            owner2ManDays.put(owner, 0);
            owner2FinishedCost.put(owner, 0.0);
        }

        owner2ManDays.put(owner, owner2ManDays.get(owner) + cost);
        owner2FinishedCost.put(owner, owner2FinishedCost.get(owner) + finishedCost);
        this.cost += cost;
    }

    @Override
    public double getFinishedCost() {
        double finishedCost = this.owner2FinishedCost.values()
            .stream().reduce((a, b) -> a + b).get();

        return finishedCost;
    }

    @Override
    public int getProgress() {
        return Double.valueOf(getFinishedCost() * 100 / cost).intValue();
    }

    @Override
    public boolean isComposite() {
        return true;
    }
}
