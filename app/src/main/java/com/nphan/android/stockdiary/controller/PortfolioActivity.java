package com.nphan.android.stockdiary.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PortfolioActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PortfolioActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PortfolioFragment.newInstance();
    }
}
