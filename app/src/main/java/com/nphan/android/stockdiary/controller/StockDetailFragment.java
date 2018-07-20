package com.nphan.android.stockdiary.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSingleton;

import java.util.HashMap;

public class StockDetailFragment extends Fragment{

    private final static String ARG_TICKER = "arg_ticker";

    private HashMap<String, StockItem> mCachedStockItems = new HashMap<>();
    private StockItem mStockItem;
    private boolean mIsCached = false;

    private String mTicker;

    private RecyclerView mRecyclerView;

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
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new RecyclerAdapter());

        return view;
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {

        public RecyclerHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            super(inflater.inflate(layoutId, parent, false));

            if (layoutId == R.layout.list_item_stock_detail_title) {
                mTickerTextView = itemView.findViewById(R.id.ticker);
                mTickerTextView.setText(mTicker);

                mCompanyNameTextView = itemView.findViewById(R.id.company_name);
                mCompanyNameTextView.setText(mStockItem.getCompanyName());

                mPriceTextView = itemView.findViewById(R.id.last_price);
            }

            else if (layoutId == R.layout.list_item_stock_detail_company_detail) {
                mSectorTextView = itemView.findViewById(R.id.sector);
                mIndustryTextView = itemView.findViewById(R.id.industry);
                mCEOTextView = itemView.findViewById(R.id.CEO);
                mExchangeTextView = itemView.findViewById(R.id.exchange);
                mDescriptionTextView = itemView.findViewById(R.id.description);

                mSectorTextView.setText(mStockItem.getSector());
                mIndustryTextView.setText(mStockItem.getIndustry());
                mCEOTextView.setText(mStockItem.getCEO());
                mExchangeTextView.setText(mStockItem.getExchange());
                mDescriptionTextView.setText(mStockItem.getDescription());
            }

        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        @NonNull
        @Override
        public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new RecyclerHolder(layoutInflater, parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {
            int viewType = R.layout.list_item_stock_detail_title;
            if (position == 1) {
                viewType = R.layout.list_item_stock_detail_graph;
            }
            else if (position == 2) {
                viewType = R.layout.list_item_stock_detail_key_stats;
            }
            else if (position == 3) {
                viewType = R.layout.list_item_stock_detail_company_detail;
            }
            return viewType;
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
            //setCompanyDetail(stockItem);
            mRecyclerView.setAdapter(new RecyclerAdapter());
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
