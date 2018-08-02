package com.nphan.android.stockdiary.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.nphan.android.stockdiary.database.TradeDbSchema.TradeTable;
import com.nphan.android.stockdiary.model.TradeItem;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class TradeCursorWrapper extends CursorWrapper {
    public TradeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TradeItem getTradeItem() {
        String uuidString = getString(getColumnIndex(TradeTable.Cols.UUID));
        String buyOrSell = getString(getColumnIndex(TradeTable.Cols.BUY_OR_SELL));
        String ticker = getString(getColumnIndex(TradeTable.Cols.TICKER));
        int quantity = getInt(getColumnIndex(TradeTable.Cols.QUANTITY));
        long timeStamp = getLong(getColumnIndex(TradeTable.Cols.DATE));
        Float price = getFloat(getColumnIndex(TradeTable.Cols.PRICE));

        TradeItem tradeItem = new TradeItem(UUID.fromString(uuidString));
        tradeItem.setBuyOrSell(buyOrSell);
        tradeItem.setTicker(ticker);
        tradeItem.setQuantity(quantity);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        tradeItem.setCalendar(calendar);
        tradeItem.setPrice(price);

        return tradeItem;
    }
}
