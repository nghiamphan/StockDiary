package com.nphan.android.stockdiary.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nphan.android.stockdiary.database.TradeBaseHelper;
import com.nphan.android.stockdiary.database.TradeCursorWrapper;
import com.nphan.android.stockdiary.database.TradeDbSchema.TradeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeSingleton {
    private static TradeSingleton sTradeSingleton;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TradeSingleton get(Context context) {
        if (sTradeSingleton == null) {
            sTradeSingleton = new TradeSingleton(context);
        }
        return sTradeSingleton;
    }

    private TradeSingleton(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TradeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addTrade(TradeItem item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(TradeTable.NAME, null, values);
    }

    public void updateTrade(TradeItem item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(TradeTable.NAME, values,
                TradeTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }

    public void deleteTrade(TradeItem item) {
        mDatabase.delete(TradeTable.NAME,
                TradeTable.Cols.UUID + " = ?",
                new String[] {item.getId().toString()});
    }

    public TradeItem getTradeById(UUID id) {
        TradeCursorWrapper cursor = queryTrades(
                TradeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTradeItem();
        }
        finally {
            cursor.close();
        }
    }

    public List<TradeItem> getTradesByTicker(String ticker) {

        List<TradeItem> tradeItems = new ArrayList<>();

        TradeCursorWrapper cursor = queryTrades(
                TradeTable.Cols.TICKER + " = ?",
                new String[] {ticker}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tradeItems.add(cursor.getTradeItem());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return tradeItems;
    }

    private static ContentValues getContentValues(TradeItem item) {
        ContentValues values = new ContentValues();
        values.put(TradeTable.Cols.UUID, item.getId().toString());
        values.put(TradeTable.Cols.BUY_OR_SELL, item.getBuyOrSell());
        values.put(TradeTable.Cols.TICKER, item.getTicker());
        values.put(TradeTable.Cols.QUANTITY, item.getQuantity());
        values.put(TradeTable.Cols.DATE, item.getDate().getTime());
        values.put(TradeTable.Cols.PRICE, item.getPrice());

        return values;
    }

    private TradeCursorWrapper queryTrades(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TradeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null,
                null
        );

        return new TradeCursorWrapper(cursor);
    }
}