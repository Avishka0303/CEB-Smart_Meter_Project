package com.example.predatorx21.cebsmartmeter.utilities;

public class ConsumptionCharge {

    private static final double BELOW60[] = {2.50, 4.85};
    private static final double BELOW60FIX[] = {30, 60};

    private static final double ABOVE60[] = {7.85, 10.00, 27.75, 32.00, 45.00};
    private static final double ABOVE60FIX[] = {0, 90, 480, 480, 540};

    private static double charge[]=new double[3];

    public static double[] UsageInCharge(double unitskwh) {
        if (unitskwh <= 30) {
            charge[0] = unitskwh * BELOW60[0];
            charge[1] = BELOW60FIX[0];
            charge[2] = charge[0] + charge[1];
        } else if (unitskwh <= 60) {
            charge[0] = 30 * BELOW60[0] + (unitskwh-30) * BELOW60[1];
            charge[1] = BELOW60FIX[1];
            charge[2] = charge[0] + charge[1];
        } else if (unitskwh <= 90) {
            charge[0] = 60 * ABOVE60[0] + (unitskwh - 60) * ABOVE60[1];
            charge[1] = ABOVE60FIX[2];
            charge[2] = charge[0] + charge[1];
        } else if (unitskwh <= 120) {
            charge[0] = 60 * ABOVE60[0] + 30 * ABOVE60[1] + (unitskwh - 90) * ABOVE60[2];
            charge[1] = ABOVE60FIX[3];
            charge[2] = charge[0] + charge[1];
        } else if (unitskwh <= 180) {
            charge[0] = 60 * ABOVE60[0] + 30 * ABOVE60[1] + 30 * ABOVE60[2] + (unitskwh - 120) * ABOVE60[3];
            charge[1] = ABOVE60FIX[4];
            charge[2] = charge[0] + charge[1];
        } else if (unitskwh >180) {
            charge[0] = 60 * ABOVE60[0] + 30 * ABOVE60[1] + 30 * ABOVE60[2] + 30 * ABOVE60[3]+ (unitskwh - 180) * ABOVE60[4];
            charge[1] = ABOVE60FIX[4];
            charge[2] = charge[0] + charge[1];
        }
        return charge;
    }
}
