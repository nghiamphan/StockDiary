package com.nphan.android.stockdiary.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class TransactionDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_TICKER = "extra_ticker";

    public static Intent newIntent(Context context, String ticker) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(EXTRA_TICKER, ticker);
        return intent;
    }

    public static Intent newIntent(Context context, UUID id, String ticker) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TICKER, ticker);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID id = (UUID) getIntent().getSerializableExtra(EXTRA_ID);
        String ticker = getIntent().getStringExtra(EXTRA_TICKER);
        if (id == null) {
            return TransactionDetailFragment.newInstance(ticker);
        }
        else {
            return TransactionDetailFragment.newInstance(id, ticker);
        }
    }
}
