package com.nphan.android.stockdiary;

import android.support.v4.app.Fragment;

public class MainMenuActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainMenuFragment.newInstance();
    }
}
