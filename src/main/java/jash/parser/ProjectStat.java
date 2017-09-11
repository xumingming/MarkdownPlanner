package jash.parser;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project Statistics
 */
public class ProjectStat {
    private double progress;

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

            DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
            return new Double(df2.format(finishedCost * 100 / totalCost)).doubleValue();
        }

        public void addTotalCost(int cost) {
            this.totalCost += cost;
        }

        public void addFinishedCost(double cost) {
            this.finishedCost += cost;
        }
    }
}
