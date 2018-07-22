package com.nphan.android.stockdiary.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.helper.NumberFormatHelper;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSingleton;

import java.util.HashMap;
import java.util.Locale;

public class StockDetailFragment extends Fragment{

    private final static String ARG_TICKER = "arg_ticker";

    private HashMap<String, StockItem> mCachedStockItems = new HashMap<>();
    private StockItem mStockItem;

    private String mTicker;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    private TextView mTickerTextView;
    private TextView mCompanyNameTextView;
    private TextView mPriceTextView;
    private TextView mPriceChangeTextView;

    private TextView mSectorTextView;
    private TextView mIndustryTextView;
    private TextView mCEOTextView;
    private TextView mExchangeTextView;
    private TextView mDescriptionTextView;

    private TextView mOpenTextView;
    private TextView mHighTextView;
    private TextView mLowTextView;
    private TextView m52WeekHighTextView;
    private TextView m52WeekLowTextView;
    private TextView mVolumeTextView;
    private TextView mAvgVolumeTextView;
    private TextView mMarketCapTextView;
    private TextView mBetaTextView;
    private TextView mEpsTextView;
    private TextView mEpsDateTextView;
    private TextView mDividendYieldTextView;
    private TextView mPriceEarningTextView;
    private TextView mPriceToBookTextView;

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
        if (getArguments() != null) {
            mTicker = getArguments().getString(ARG_TICKER);
        }
        if (!mCachedStockItems.containsKey(mTicker)) {
            mStockItem = new StockItem();
            mStockItem.setTicker(mTicker);
            new FetchCompanyInfoTask().execute();
            new FetchKeyStatsTask().execute();
        }
        else {
            mStockItem = mCachedStockItems.get(mTicker);
        }
        new FetchStockQuoteTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        if (mRecyclerAdapter == null) {
            mRecyclerAdapter = new RecyclerAdapter();
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
        else {
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {

        private int mLayoutId;

        public RecyclerHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            super(inflater.inflate(layoutId, parent, false));
            mLayoutId = layoutId;
        }

        private void bind() {
            if (mLayoutId == R.layout.list_item_stock_detail_title) {
                mTickerTextView = itemView.findViewById(R.id.ticker);
                mTickerTextView.setText(mTicker);

                mCompanyNameTextView = itemView.findViewById(R.id.company_name);
                mCompanyNameTextView.setText(mStockItem.getCompanyName());

                mPriceTextView = itemView.findViewById(R.id.last_price);

                Spannable spannableString = new SpannableString(String.format(Locale.US,"$%.2f", mStockItem.getPrice()));
                spannableString.setSpan(new RelativeSizeSpan(0.7f), 0 , 1, 0);
                mPriceTextView.setText(spannableString);

                mPriceChangeTextView = itemView.findViewById(R.id.price_change);
                String changePrefix;
                if (mStockItem.getChangeToday() < 0) {
                    changePrefix = "-";
                }
                else {
                    changePrefix = "+";
                }
                mPriceChangeTextView.setText(String.format(Locale.US, changePrefix + "$%.2f" + "  " + "(%.2f%%)", Math.abs(mStockItem.getChangeToday()), mStockItem.getChangePercent()*100));
                if (mStockItem.getChangeToday() < 0) {
                    mPriceChangeTextView.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    mPriceChangeTextView.setTextColor(getResources().getColor(R.color.green));
                }
            }

            else if (mLayoutId == R.layout.list_item_stock_detail_key_stats) {
                mOpenTextView = itemView.findViewById(R.id.open_price);
                mHighTextView = itemView.findViewById(R.id.high_price);
                mLowTextView = itemView.findViewById(R.id.low_price);
                m52WeekHighTextView = itemView.findViewById(R.id.year_high);
                m52WeekLowTextView = itemView.findViewById(R.id.year_low);
                mVolumeTextView = itemView.findViewById(R.id.volume);
                mAvgVolumeTextView = itemView.findViewById(R.id.average_volume);

                mMarketCapTextView = itemView.findViewById(R.id.market_cap);
                mBetaTextView = itemView.findViewById(R.id.beta);
                mEpsTextView = itemView.findViewById(R.id.eps);
                mEpsDateTextView = itemView.findViewById(R.id.eps_date);
                mDividendYieldTextView = itemView.findViewById(R.id.dividend_yield);
                mPriceEarningTextView = itemView.findViewById(R.id.price_earning);
                mPriceToBookTextView = itemView.findViewById(R.id.price_to_book);

                mOpenTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getOpen()));
                mHighTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getHighToday()));
                mLowTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getLowToday()));
                m52WeekHighTextView.setText(String.format(Locale.US, "%.2f", mStockItem.get52WeekHigh()));
                m52WeekLowTextView.setText(String.format(Locale.US, "%.2f", mStockItem.get52WeekLow()));
                mVolumeTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getVolume()));
                mAvgVolumeTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getAvgVolume()));

                mMarketCapTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getMarketCap()));
                mBetaTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getBeta()));
                mEpsTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getLatestEPS()));
                mEpsDateTextView.setText(mStockItem.getLatestEPSDate());
                mDividendYieldTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getDividendYield()));
                mPriceEarningTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getPERatio()));
                mPriceToBookTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getPriceToBook()));
            }

            else if (mLayoutId == R.layout.list_item_stock_detail_company_detail) {
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
            holder.bind();
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
            setupAdapter();

            mStockItem.setCompanyName(stockItem.getCompanyName());
            mStockItem.setSector(stockItem.getSector());
            mStockItem.setIndustry(stockItem.getIndustry());
            mStockItem.setCEO(stockItem.getCEO());
            mStockItem.setExchange(stockItem.getExchange());
            mStockItem.setDescription(stockItem.getDescription());

            mCachedStockItems.put(mTicker, mStockItem);
        }
    }

    private class FetchKeyStatsTask extends AsyncTask<Void, Void, StockItem> {
        /*
        Fetch key stats: 52w high, 52w low, beta, eps, eps date, dividend yield, price to book
         */

        @Override
        protected StockItem doInBackground(Void... voids) {
            return new DataFetch().fetchKeyStats(mTicker);
        }

        @Override
        protected void onPostExecute(StockItem stockItem) {
            setupAdapter();

            mStockItem.set52WeekHigh(stockItem.get52WeekHigh());
            mStockItem.set52WeekLow(stockItem.get52WeekLow());
            mStockItem.setBeta(stockItem.getBeta());
            mStockItem.setLatestEPS(stockItem.getLatestEPS());
            mStockItem.setLatestEPSDate(stockItem.getLatestEPSDate());
            mStockItem.setDividendYield(stockItem.getDividendYield());
            mStockItem.setPriceToBook(stockItem.getPriceToBook());

            mCachedStockItems.put(mTicker, mStockItem);
        }
    }

    private class FetchStockQuoteTask extends AsyncTask<Void, Void, StockItem> {
        /*
        Fetch key stats: open, high, low, volume, avg volume, market cap, pe ratio, price, change, change percent
         */

        @Override
        protected StockItem doInBackground(Void... voids) {
            return new DataFetch().fetchStockQuote(mTicker);
        }

        @Override
        protected void onPostExecute(StockItem stockItem) {
            setupAdapter();
            Log.i("HIH", "updated");

            mStockItem.setPrice(stockItem.getPrice());
            mStockItem.setChangeToday(stockItem.getChangeToday());
            mStockItem.setChangePercent(stockItem.getChangePercent());

            mStockItem.setOpen(stockItem.getOpen());
            mStockItem.setHighToday(stockItem.getHighToday());
            mStockItem.setLowToday(stockItem.getLowToday());
            mStockItem.setVolume(stockItem.getVolume());
            mStockItem.setAvgVolume(stockItem.getAvgVolume());
            mStockItem.setMarketCap(stockItem.getMarketCap());
            mStockItem.setPERatio(stockItem.getPERatio());

            mCachedStockItems.put(mTicker, mStockItem);
        }
    }
}
