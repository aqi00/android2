package com.example.media.util;

import java.text.NumberFormat;

public class FlowUtil {
    private final static float divUnit = 1024.00f;
    private final static int cmpUnit = 1000;
    public final static String ZEROB = "0B";

    // 格式化流量字符串
    public static String BToShowString(long flowB, int decimal) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(decimal);
        if (flowB <= 0) {
            return ZEROB;
        }
        if (flowB < cmpUnit) {
            return flowB + "B";
        }
        if (flowB / cmpUnit < cmpUnit) {
            double res = (double) flowB / divUnit;
            return ddf1.format(res) + "K";
        }
        if (flowB / cmpUnit / cmpUnit < cmpUnit) {
            double res = (double) flowB / divUnit;
            res /= divUnit;
            return ddf1.format(res) + "M";
        }
        double res = (double) flowB / divUnit;
        res /= divUnit;
        res /= divUnit;
        return ddf1.format(res) + "G";
    }

}
