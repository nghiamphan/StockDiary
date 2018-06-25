package com.nphan.android.stockdiary;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class StockSharedPreferences {
    /*
    PREF_STOCK_LIST: the list of all stocks in the api, around 8000
    PREF_WATCH_LIST: the list of stocks in the watchlist
     */
    private static final String PREF_STOCK_LIST = "stock_list";

    public static void setStockList(Context context, List<StockItem> stockItems) {
        Gson gson = new Gson();
        String stockList = gson.toJson(stockItems);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_STOCK_LIST, stockList)
                .apply();
    }

    public static List<StockItem> getStockList(Context context) {
        String stockList = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_STOCK_LIST, null);
        if (stockList == null) {
            return null;
        }
        else {
           Gson gson = new Gson();
           StockItem[] items = gson.fromJson(stockList, StockItem[].class);
           List<StockItem> stockItems = Arrays.asList(items);
           return stockItems;
        }
    }
}
