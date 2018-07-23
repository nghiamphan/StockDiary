package com.nphan.android.stockdiary.helper;

import android.graphics.DashPathEffect;

import com.nphan.android.stockdiary.R;
import com.robinhood.spark.SparkAdapter;

import java.util.List;

public class MySparkAdapter extends SparkAdapter {
    private List<Float> mPrices;
    private Float mPreviousClose;

    public MySparkAdapter(List<Float> prices, Float previousClose) {
        mPrices = prices;
        mPreviousClose = previousClose;
    }

    @Override
    public int getCount() {
        return mPrices.size();
    }

    @Override
    public Object getItem(int index) {
        return mPrices.get(index);
    }

    @Override
    public float getY(int index) {
        if (mPrices.get(index) == null) {
            return 0;
        }
        return mPrices.get(index);
    }

    @Override
    public float getBaseLine() {
        return mPreviousClose;
    }

    @Override
    public boolean hasBaseLine() {
        return getBaseLine() != 0;
    }

    public int getColorId() {
        int colorId = R.color.green;
        if (mPrices.get(mPrices.size()-1) < mPreviousClose) {
            colorId = R.color.red;
        }
        return colorId;
    }

    public DashPathEffect getDottedBaseline() {
        return new DashPathEffect(new float[] {5, 10}, 0);
    }
}
