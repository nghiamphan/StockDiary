package com.nphan.android.stockdiary.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class TradeItem {
    private UUID mId;
    private String mBuyOrSell;
    private String mTicker;
    private int mQuantity;
    private Calendar mCalendar;
    private Float mPrice;

    public TradeItem() {
        this(UUID.randomUUID());
    }

    public TradeItem(UUID id) {
        mId = id;
        mBuyOrSell = "BUY";
        mQuantity = 0;
        mCalendar = new GregorianCalendar();
    }

    public UUID getId() {
        return mId;
    }

    public String getBuyOrSell() {
        return mBuyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        mBuyOrSell = buyOrSell;
    }

    public String getTicker() {
        return mTicker;
    }

    public void setTicker(String ticker) {
        mTicker = ticker;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        mCalendar = calendar;
    }

    public Float getPrice() {
        return mPrice;
    }

    public void setPrice(Float price) {
        mPrice = price;
    }
}
