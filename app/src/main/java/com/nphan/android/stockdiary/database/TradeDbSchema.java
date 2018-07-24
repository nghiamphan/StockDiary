package com.nphan.android.stockdiary.database;

public class TradeDbSchema {
    public static final class TradeTable {
        public static final String NAME = "trades";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String BUY_OR_SELL = "buy_or_sell";
            public static final String TICKER = "ticker";
            public static final String QUANTITY = "quantity";
            public static final String DATE = "date";
            public static final String PRICE = "price";
        }
    }
}
