package com.nphan.android.stockdiary.helper;

import java.util.Locale;

public class NumberFormatHelper {

    private static final float billion = (float) Math.pow(10, 9);
    private static final float million = (float) Math.pow(10, 6);
    private static final float thousand = (float) Math.pow(10, 3);

    public String formatBigNumber(Float number) {
        boolean negative = number < 0;
        String retString;
        number = Math.abs(number);

        if (number > billion) {
            Float n = number / billion;
            retString = String.format(Locale.US, "%.2f", n) + "B";
        }
        else if (number > million) {
            Float n = number / million;
            retString = String.format(Locale.US,"%.2f", n) + "M";
        }
        else if (number > thousand) {
            Float n = number / thousand;
            retString = n.toString() + "K";
        }
        else {
            retString = String.format(Locale.US,"%.2f", number);
        }

        if (negative) {
            retString = "-" + retString;
        }
        return retString;
    }
}
