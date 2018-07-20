package com.nphan.android.stockdiary.model;

public class StockItem {
    private String mTicker;
    private String mCompanyName;
    private Float mPrice;
    private Float mChangeToday;
    private Float mChangePercent;
    private String mSector;
    private String mIndustry;
    private String mDescription;
    private String mCEO;
    private String mExchange;
    private Float mBeta;
    private Float mMarketCap;
    private Float mPERatio;
    private Float mLatestEPS;
    private String mLatestEPSDate;
    private Float mDividendYield;
    private Float mAvgVolume;
    private Float mHighToday;
    private Float mLowToday;
    private Float mOpen;
    private Float mVolume;
    private Float m52WeekHigh;
    private Float m52WeekLow;
    private Float mPriceToBook;

    public StockItem () {
        mPrice = new Float(0);
        mChangeToday = new Float(0);
        mChangePercent = new Float(0);

        mCompanyName = "_";
        mSector = "_";
        mIndustry = "_";
        mCEO = "_";
        mExchange = "_";
        mDescription = "_";

        mOpen = new Float(0);
        mHighToday = new Float(0);
        mLowToday = new Float(0);
        m52WeekHigh = new Float(0);
        m52WeekLow = new Float(0);
        mAvgVolume = new Float(0);
        mVolume = new Float(0);

        mMarketCap = new Float(0);
        mBeta = new Float(0);
        mLatestEPS = new Float(0);
        mLatestEPSDate = "_";
        mDividendYield = new Float(0);
        mPERatio = new Float(0);
        mPriceToBook = new Float(0);
    }

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

    public Float getChangeToday() {
        return mChangeToday;
    }

    public void setChangeToday(Float changeToday) {
        mChangeToday = changeToday;
    }

    public Float getChangePercent() {
        return mChangePercent;
    }

    public void setChangePercent(Float changePercent) {
        mChangePercent = changePercent;
    }

    public String getSector() {
        return mSector;
    }

    public void setSector(String sector) {
        mSector = sector;
    }

    public String getIndustry() {
        return mIndustry;
    }

    public void setIndustry(String industry) {
        mIndustry = industry;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getCEO() {
        return mCEO;
    }

    public void setCEO(String CEO) {
        mCEO = CEO;
    }

    public String getExchange() {
        return mExchange;
    }

    public void setExchange(String exchange) {
        mExchange = exchange;
    }

    public Float getBeta() {
        return mBeta;
    }

    public void setBeta(Float beta) {
        mBeta = beta;
    }

    public Float getMarketCap() {
        return mMarketCap;
    }

    public void setMarketCap(Float marketCap) {
        mMarketCap = marketCap;
    }

    public Float getPERatio() {
        return mPERatio;
    }

    public void setPERatio(Float PERatio) {
        mPERatio = PERatio;
    }

    public Float getLatestEPS() {
        return mLatestEPS;
    }

    public void setLatestEPS(Float latestEPS) {
        this.mLatestEPS = latestEPS;
    }

    public String  getLatestEPSDate() {
        return mLatestEPSDate;
    }

    public void setLatestEPSDate(String latestEPSDate) {
        this.mLatestEPSDate = latestEPSDate;
    }

    public Float getDividendYield() {
        return mDividendYield;
    }

    public void setDividendYield(Float dividendYield) {
        mDividendYield = dividendYield;
    }

    public Float getAvgVolume() {
        return mAvgVolume;
    }

    public void setAvgVolume(Float avgVolume) {
        mAvgVolume = avgVolume;
    }

    public Float getHighToday() {
        return mHighToday;
    }

    public void setHighToday(Float highToday) {
        mHighToday = highToday;
    }

    public Float getLowToday() {
        return mLowToday;
    }

    public void setLowToday(Float lowToday) {
        mLowToday = lowToday;
    }

    public Float getOpen() {
        return mOpen;
    }

    public void setOpen(Float open) {
        mOpen = open;
    }

    public Float getVolume() {
        return mVolume;
    }

    public void setVolume(Float volume) {
        mVolume = volume;
    }

    public Float get52WeekHigh() {
        return m52WeekHigh;
    }

    public void set52WeekHigh(Float m52WeekHigh) {
        this.m52WeekHigh = m52WeekHigh;
    }

    public Float get52WeekLow() {
        return m52WeekLow;
    }

    public void set52WeekLow(Float m52WeekLow) {
        this.m52WeekLow = m52WeekLow;
    }

    public Float getPriceToBook() {
        return mPriceToBook;
    }

    public void setPriceToBook(Float priceToBook) {
        mPriceToBook = priceToBook;
    }
}
