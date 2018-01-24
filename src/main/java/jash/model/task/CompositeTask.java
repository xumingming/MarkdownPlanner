package jash.model.task;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import jash.model.Header;

public class CompositeTask extends AbstractTask {
    private Map<String, Integer> owner2Cost = new HashMap<>();
    private Map<String, Double> owner2FinishedCost = new HashMap<>();

    public CompositeTask(Header header, String name) {
        this(header, name, null);
    }

    public CompositeTask(Header header, String name, LocalDate projectStartDate) {
        this.name = name;
        this.header = header;
        this.projectStartDate = projectStartDate;
    }

    @Override
    public String getOwner() {
        return Joiner.on("/").join(
            this.owner2Cost.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
        );
    }

    public void addOwnerCost(String owner, int cost, double finishedCost) {
        if (!owner2Cost.containsKey(owner)) {
            owner2Cost.put(owner, 0);
            owner2FinishedCost.put(owner, 0.0);
        }

        owner2Cost.put(owner, owner2Cost.get(owner) + cost);
        owner2FinishedCost.put(owner, owner2FinishedCost.get(owner) + finishedCost);
    }

    @Override
    public int getCost() {
        double ret = this.owner2Cost.values()
            .stream().reduce((a, b) -> a + b).get();

        return Double.valueOf(ret).intValue();
    }

    @Override
    public double getFinishedCost() {
        double finishedCost = this.owner2FinishedCost.values()
            .stream().reduce((a, b) -> a + b).get();

        return finishedCost;
    }

    @Override
    public int getProgress() {
        return Double.valueOf(getFinishedCost() * 100 / getCost()).intValue();
    }

    @Override
    public boolean isComposite() {
        return true;
    }
}
