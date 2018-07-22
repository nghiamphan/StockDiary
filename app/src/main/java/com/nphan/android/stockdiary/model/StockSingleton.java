package com.nphan.android.stockdiary.model;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public class StockSingleton {

    private static StockSingleton sStockSingleton;

    /*
    mCachedStockItems: temporarily store StockItem object whose stock details have been fetched in StockDetailActivity
    - key: ticker
    - value: StockItem object

    mChartPrices:
    - key: ticker
    - value: list of prices for charting

    mPreviousPrices:
    - key: ticker
    - value: list of prices for previous day
     */

    private HashMap<String, StockItem> mCachedStockItems = new HashMap<>();
    private HashMap<String, List<Float>> mChartPrices = new HashMap<>();
    private HashMap<String, Float> mPreviousPrices = new HashMap<>();

    private StockSingleton(Context context) {
    }

    public static StockSingleton get(Context context) {
        if (sStockSingleton == null) {
            sStockSingleton = new StockSingleton(context);
        }
        return sStockSingleton;
    }

    public HashMap<String, StockItem> getCachedStockItems() {
        return mCachedStockItems;
    }

    public HashMap<String, List<Float>> getChartPrices() {
        return mChartPrices;
    }

    public HashMap<String, Float> getPreviousPrices() {
        return mPreviousPrices;
    }
}
