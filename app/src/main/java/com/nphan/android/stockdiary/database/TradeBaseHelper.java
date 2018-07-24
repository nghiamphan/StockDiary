package com.nphan.android.stockdiary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nphan.android.stockdiary.database.TradeDbSchema.TradeTable;

public class TradeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tradeBase.db";

    public TradeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TradeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TradeTable.Cols.UUID + ", " +
                TradeTable.Cols.BUY_OR_SELL + ", " +
                TradeTable.Cols.TICKER + ", " +
                TradeTable.Cols.QUANTITY + ", " +
                TradeTable.Cols.DATE + ", " +
                TradeTable.Cols.PRICE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
