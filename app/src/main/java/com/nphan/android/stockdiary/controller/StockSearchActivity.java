package com.nphan.android.stockdiary.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class StockSearchActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, StockSearchActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return StockSearchFragment.newInstance();
    }
}
