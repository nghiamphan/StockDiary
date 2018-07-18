package com.nphan.android.stockdiary.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockDetailFragment extends Fragment{

    private final static String ARG_TICKER = "arg_ticker";

    private HashMap<String, StockItem> mCachedStockItems = new HashMap<>();
    private StockItem mStockItem;
    private boolean mIsCached = false;

    private String mTicker;

    private TextView mTickerTextView;
    private TextView mCompanyNameTextView;
    private TextView mPriceTextView;

    private TextView mSectorTextView;
    private TextView mIndustryTextView;
    private TextView mCEOTextView;
    private TextView mExchangeTextView;
    private TextView mDescriptionTextView;

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

        mCachedStockItems = StockSingleton.get(getActivity()).getCachedStockItems();
        mTicker = getArguments().getString(ARG_TICKER);
        if (!mCachedStockItems.containsKey(mTicker)) {
            mStockItem = new StockItem();
            mStockItem.setTicker(mTicker);
            new FetchCompanyInfoTask().execute();
        }
        else {
            mStockItem = mCachedStockItems.get(mTicker);
            mIsCached = true;
            Log.i("HIHI", "Cached");
            Log.i("HIHI", mStockItem.getCompanyName());
        }
        //new FetchStockCompanyNameAndPriceTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        mTickerTextView = view.findViewById(R.id.ticker);
        mTickerTextView.setText(mTicker);

        mCompanyNameTextView = view.findViewById(R.id.company_name);
        mPriceTextView = view.findViewById(R.id.last_price);

        mSectorTextView = view.findViewById(R.id.sector);
        mIndustryTextView = view.findViewById(R.id.industry);
        mCEOTextView = view.findViewById(R.id.CEO);
        mExchangeTextView = view.findViewById(R.id.exchange);
        mDescriptionTextView = view.findViewById(R.id.description);

        if (mIsCached) {
            setCompanyDetail(mStockItem);
        }

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

    private void setCompanyDetail(StockItem stockItem) {
        mCompanyNameTextView.setText(stockItem.getCompanyName());
        mSectorTextView.setText(stockItem.getSector());
        mIndustryTextView.setText(stockItem.getIndustry());
        mCEOTextView.setText(stockItem.getCEO());
        mExchangeTextView.setText(stockItem.getExchange());
        mDescriptionTextView.setText(stockItem.getDescription());
    }

    private class FetchCompanyInfoTask extends AsyncTask<Void, Void, StockItem> {
        /*
        Fetch company information: company name, sector, industry, CEO, description
         */

        @Override
        protected StockItem doInBackground(Void... voids) {
            return new DataFetch().fetchCompanyInfo(mTicker);
        }

        @Override
        protected void onPostExecute(StockItem stockItem) {
            setCompanyDetail(stockItem);

            mStockItem.setCompanyName(stockItem.getCompanyName());
            mStockItem.setSector(stockItem.getSector());
            mStockItem.setIndustry(stockItem.getIndustry());
            mStockItem.setCEO(stockItem.getCEO());
            mStockItem.setExchange(stockItem.getExchange());
            mStockItem.setDescription(stockItem.getDescription());
            mCachedStockItems.put(mTicker, mStockItem);
        }
    }
}
