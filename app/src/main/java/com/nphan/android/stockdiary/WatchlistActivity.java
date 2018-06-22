package com.nphan.android.stockdiary;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class WatchlistActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, WatchlistActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return WatchlistFragment.newInstance();
    }
}
