package com.nphan.android.stockdiary;

public class StockItem {
    private String mTicker;
    private String mCompanyName;
    private Float mPrice;

    public String getTicker() {
        return mTicker;
    }

    public void setTicker(String ticker) {
        this.mTicker = ticker;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public Float getPrice() {
        return mPrice;
    }

    public void setPrice(Float price) {
        this.mPrice = price;
    }
}
