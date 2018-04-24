package org.xumingmingv.markdownplanner.model;

import java.text.DecimalFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStat {
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

    private static double formatDouble(double d) {
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        return new Double(df2.format(d)).doubleValue();
    }
}