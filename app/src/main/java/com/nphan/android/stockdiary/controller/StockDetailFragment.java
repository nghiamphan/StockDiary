package com.nphan.android.stockdiary.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.StockItem;

import java.util.ArrayList;
import java.util.List;

public class StockDetailFragment extends Fragment{

    private final static String ARG_TICKER = "arg_ticker";

    private StockItem mStockItem;

    private String mTicker;

    private TextView mTickerTextView;
    private TextView mCompanyNameTextView;
    private TextView mPriceTextView;

    public static StockDetailFragment newInstance(String ticker) {

        Bundle args = new Bundle();
        args.putString(ARG_TICKER, ticker);

        StockDetailFragment fragment = new StockDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStockItem = new StockItem();

        mTicker = getArguments().getString(ARG_TICKER);
        mStockItem.setTicker(mTicker);

        new FetchStockCompanyNameAndPriceTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        mTickerTextView = view.findViewById(R.id.ticker);
        mTickerTextView.setText(mTicker);

        mCompanyNameTextView = view.findViewById(R.id.company_name);
        mPriceTextView = view.findViewById(R.id.last_price);


        return view;
    }

    private class FetchStockCompanyNameAndPriceTask extends AsyncTask<Void, Void, StockItem> {
        /*
        Fetch stock details
         */

        @Override
        protected StockItem doInBackground(Void... voids) {
            List<String> oneTickerList = new ArrayList<>();
            oneTickerList.add(mTicker);
            return new DataFetch().fetchCompanyNameAndPrice(oneTickerList).get(0);
        }

        @Override
        protected void onPostExecute(StockItem stockItem) {
            mStockItem.setCompanyName(stockItem.getCompanyName());
            mCompanyNameTextView.setText(mStockItem.getCompanyName());

            mStockItem.setPrice(stockItem.getPrice());
            mPriceTextView.setText(mStockItem.getPrice().toString());
        }
    }
}
