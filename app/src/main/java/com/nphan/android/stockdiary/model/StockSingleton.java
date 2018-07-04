package com.nphan.android.stockdiary.model;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public class StockSingleton {

    private static StockSingleton sStockSingleton;

    /*
    mChartPrices:
    - key: ticker
    - value: list of prices for charting

    mPreviousPrices:
    - key: ticker
    - value: list of prices for previous day
     */

    private HashMap<String, List> mChartPrices = new HashMap<>();
    private HashMap<String, Float> mPreviousPrices = new HashMap<>();

    private StockSingleton(Context context) {
    }

    public static StockSingleton get(Context context) {
        if (sStockSingleton == null) {
            sStockSingleton = new StockSingleton(context);
        }
        return sStockSingleton;
    }

    public HashMap<String, List> getChartPrices() {
        return mChartPrices;
    }

    public HashMap<String, Float> getPreviousPrices() {
        return mPreviousPrices;
    }
}
