package com.nphan.android.stockdiary.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class StockDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_TICKER = "extra_ticker";

    public static Intent newIntent(Context context, String ticker) {
        Intent intent = new Intent(context, StockDetailActivity.class);
        intent.putExtra(EXTRA_TICKER, ticker);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String ticker = getIntent().getStringExtra(EXTRA_TICKER);
        return StockDetailFragment.newInstance(ticker);
    }
}
