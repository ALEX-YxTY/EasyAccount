package com.exercise.yxty.easyaccount.Utils;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/4/18.
 */
public class DecimalFormatUtil {

    public static String decimalFormat(int values) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df.format(values);
    }

    public static String decimalFormat(long values) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df.format(values);
    }

    public static String decimalFormat(double values) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        return df.format(values);
    }
}
