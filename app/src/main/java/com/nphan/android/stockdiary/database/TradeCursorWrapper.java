package com.nphan.android.stockdiary.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.nphan.android.stockdiary.database.TradeDbSchema.TradeTable;
import com.nphan.android.stockdiary.model.TradeItem;

import java.util.Date;

public class TradeCursorWrapper extends CursorWrapper {
    public TradeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TradeItem getTradeItem() {
        String uuidString = getString(getColumnIndex(TradeTable.Cols.UUID));
        String buyOrSell = getString(getColumnIndex(TradeTable.Cols.BUY_OR_SELL));
        String ticker = getString(getColumnIndex(TradeTable.Cols.TICKER));
        int quantity = getInt(getColumnIndex(TradeTable.Cols.QUANTITY));
        long date = getLong(getColumnIndex(TradeTable.Cols.DATE));
        Float price = getFloat(getColumnIndex(TradeTable.Cols.PRICE));

        TradeItem tradeItem = new TradeItem();
        tradeItem.setBuyOrSell(buyOrSell);
        tradeItem.setTicker(ticker);
        tradeItem.setQuantity(quantity);
        tradeItem.setDate(new Date(date));
        tradeItem.setPrice(price);

        return tradeItem;
    }
}
