package com.nphan.android.stockdiary.model;

public class StockItem {
    private String mTicker;
    private String mCompanyName;
    private Float mPrice;
    private Float mChangeToday;
    private Float mChangePercent;
    private String mSector;
    private String mIndustry;
    private String mCEO;
    private Float mBeta;
    private Float mMarketCap;
    private Float mPERatio;
    private Float mlatestEPS;
    private Float mlatestEPSDate;
    private Float mDividendYield;
    private Float mAvgVolume;
    private Float mHighToday;
    private Float mLowToday;
    private Float mOpen;
    private Float mVolume;
    private Float m52WeekHigh;
    private Float m52WeekLow;
    private Float mPriceToBook;

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
