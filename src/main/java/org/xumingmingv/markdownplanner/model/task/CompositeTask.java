package org.xumingmingv.markdownplanner.model.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import org.xumingmingv.markdownplanner.model.Header;

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
        List<String> users = new ArrayList<>(
            this.owner2Cost.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList())
        );

        if (users.size() > 5) {
            users = users.subList(0, 5);
            users.add("...");
        }

        return Joiner.on("/").join(users);
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
        Optional<Integer> ret = this.owner2Cost.values()
            .stream().reduce((a, b) -> a + b);

        return ret.isPresent() ? Double.valueOf(ret.get()).intValue() : 0;
    }

    @Override
    public double getFinishedCost() {
        Optional<Double> finishedCost = this.owner2FinishedCost.values()
            .stream().reduce((a, b) -> a + b);

        return finishedCost.isPresent() ? finishedCost.get() : 0;
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
