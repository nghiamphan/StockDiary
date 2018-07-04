package com.nphan.android.stockdiary.controller;

import android.support.v4.app.Fragment;

public class MainMenuActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainMenuFragment.newInstance();
    }
}
