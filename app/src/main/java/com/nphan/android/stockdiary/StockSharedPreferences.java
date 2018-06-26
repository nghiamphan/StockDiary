package com.nphan.android.stockdiary;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StockSharedPreferences {
    /*
    PREF_STOCK_LIST: the list of all stocks in the api, around 8000
    PREF_TICKER_WATCHLIST: the list of stock tickers in the watchlist
     */
    private static final String PREF_STOCK_LIST = "stock_list";
    private static final String PREF_TICKER_WATCHLIST = "ticker_watchlist";

    public static void setStockList(Context context, List<StockItem> stockItems) {
        Gson gson = new Gson();
        String stockListString = gson.toJson(stockItems);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_STOCK_LIST, stockListString)
                .apply();
    }

    public static List<StockItem> getStockList(Context context) {
        String stockListString = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_STOCK_LIST, null);
        if (stockListString == null) {
            return new ArrayList<>();
        }
        else {
           Gson gson = new Gson();
           StockItem[] items = gson.fromJson(stockListString, StockItem[].class);
           List<StockItem> stockItems = new ArrayList<>(Arrays.asList(items));
           return stockItems;
        }
    }

    public static void setTickerWatchlist(Context context, List<String> tickers) {
        Gson gson = new Gson();
        String tickerListString = gson.toJson(tickers);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_TICKER_WATCHLIST, tickerListString)
                .apply();
    }

    public static List<String> getTickerWatchlist(Context context) {
        String tickerListString = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_TICKER_WATCHLIST, null);
        if (tickerListString == null) {
            return new ArrayList<>();
        }
        else {
            Gson gson = new Gson();
            String[] items = gson.fromJson(tickerListString, String[].class);
            List<String> tickers = new ArrayList<>(Arrays.asList(items));
            return tickers;
        }
    }
}
