package com.nphan.android.stockdiary.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSharedPreferences;

import java.util.List;

public class MainMenuFragment extends Fragment {

    public static MainMenuFragment newInstance() {

        Bundle args = new Bundle();

        MainMenuFragment fragment = new MainMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new FetchTickerAndNameTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        Button watchlistButton = view.findViewById(R.id.main_menu_watchlist_button);
        watchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WatchlistActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        return view;
    }

    private class FetchTickerAndNameTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<StockItem> tickerAndNameList = new DataFetch().fetchStockTickerAndName();
            StockSharedPreferences.setStockList(getActivity(), tickerAndNameList);
            return null;
        }
    }
}
