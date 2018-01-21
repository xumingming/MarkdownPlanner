package jash.parser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

/**
 * Project Statistics
 */
@ToString
@EqualsAndHashCode
public class ProjectStat {
    private List<UserStat> userStats;
    private Map<String, UserStat> userStatMap;

    public ProjectStat(Map<String, UserStat> userStatMap) {
        this.userStatMap = userStatMap;
        this.userStats = userStatMap.values().stream().collect(Collectors.toList());
    }

    public PercentageStat getNotFinishedStat() {
        List<String> users = userStats.stream().map(UserStat::getUser).collect(Collectors.toList());
        List<Double> percentages = userStats.stream()
            .map(ut -> formatDouble(ut.getNotFinishedManDays()))
            .collect(Collectors.toList());
        return new PercentageStat(users, percentages);
    }

    public UserStat getUserStat(String user) {
        return userStatMap.get(user);
    }

    public UserStat getTotalStat() {
        UserStat totalStat = new UserStat();
        totalStat.setUser("-- 总记 --");
        userStats.stream()
            .forEach(stat -> {
                totalStat.addTotalCost(stat.getTotalCost());
                totalStat.addFinishedCost(stat.getFinishedCost());
            });

        return totalStat;
    }

    @Value
    public static class PercentageStat {
        private List<String> users;
        private List<Double> percentages;
    }

    @Getter
    @Setter
    @ToString
    public static class UserStat {
        private String user;
        private int totalCost;
        private double finishedCost;

        public double getProgress() {
            if (totalCost == 0) {
                return 0;
            }

            return formatDouble(finishedCost * 100 / totalCost);
        }

        public void addTotalCost(int cost) {
            this.totalCost += cost;
        }

        public void addFinishedCost(double cost) {
            this.finishedCost += cost;
        }

        public double getTotalManDays() {
            return totalCost / 2;
        }

        public double getFinishedManDays() {
            return finishedCost / 2;
        }

        public double getNotFinishedManDays() {
            return (totalCost - finishedCost) / 2;
        }
    }

    private static double formatDouble(double d) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        return new Double(df2.format(d)).doubleValue();
    }
}
