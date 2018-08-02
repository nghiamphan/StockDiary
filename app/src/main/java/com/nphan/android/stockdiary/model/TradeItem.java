package com.nphan.android.stockdiary.model;

import java.util.Date;
import java.util.UUID;

public class TradeItem {
    private UUID mId;
    private String mBuyOrSell;
    private String mTicker;
    private int mQuantity;
    private Date mDate;
    private Float mPrice;

    public TradeItem() {
        this(UUID.randomUUID());
    }

    public TradeItem(UUID id) {
        mId = id;
        mBuyOrSell = "BUY";
        mQuantity = 0;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Float getPrice() {
        return mPrice;
    }

    public void setPrice(Float price) {
        mPrice = price;
    }
}
