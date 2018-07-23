package com.nphan.android.stockdiary.controller;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.helper.MySparkAdapter;
import com.nphan.android.stockdiary.helper.NumberFormatHelper;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSharedPreferences;
import com.nphan.android.stockdiary.model.StockSingleton;
import com.robinhood.spark.SparkView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StockDetailFragment extends Fragment{

    private final static String ARG_TICKER = "arg_ticker";

    private List<String> mWatchlistTickers;

    private HashMap<String, StockItem> mCachedStockItems = new HashMap<>();
    private StockItem mStockItem;
    private String mTicker;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    private HashMap<String, List<Float>> mChartPrices = StockSingleton.get(getActivity()).getChartPrices();
    private HashMap<String, Float> mPreviousPrices = StockSingleton.get(getActivity()).getPreviousPrices();

    private FetchCompanyInfoTask mFetchCompanyInfoTask = new FetchCompanyInfoTask();
    private FetchKeyStatsTask mFetchKeyStatsTask = new FetchKeyStatsTask();
    private FetchStockQuoteTask mFetchStockQuoteTask = new FetchStockQuoteTask();
    private FetchDataChartTask mFetchDataChartTask = new FetchDataChartTask();
    private FetchPreviousPriceTask mFetchPreviousPriceTask = new FetchPreviousPriceTask();

    private ImageView mAddStockImageView;
    private int mDrawableId;

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

        mWatchlistTickers = StockSharedPreferences.getTickerWatchlist(getActivity());
        mCachedStockItems = StockSingleton.get(getActivity()).getCachedStockItems();

        if (getArguments() != null) {
            mTicker = getArguments().getString(ARG_TICKER);
        }
        if (!mCachedStockItems.containsKey(mTicker)) {
            mStockItem = new StockItem();
            mStockItem.setTicker(mTicker);
            mFetchCompanyInfoTask.execute();
            mFetchKeyStatsTask.execute();
        }
        else {
            mStockItem = mCachedStockItems.get(mTicker);
        }
        mFetchStockQuoteTask.execute();
        mFetchDataChartTask.execute(mTicker);
        mFetchPreviousPriceTask.execute(mTicker);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            activity.getSupportActionBar().setCustomView(R.layout.fragment_stock_detail_menu);
            View menuView = activity.getSupportActionBar().getCustomView();
            setupMenu(menuView);
        }
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

    @Override
    public void onPause() {
        super.onPause();
        StockSharedPreferences.setTickerWatchlist(getActivity(), mWatchlistTickers);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFetchCompanyInfoTask.cancel(true);
        mFetchKeyStatsTask.cancel(true);
        mFetchStockQuoteTask.cancel(true);
        mFetchDataChartTask.cancel(true);
        mFetchPreviousPriceTask.cancel(true);
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

    private void setupMenu(View menuView) {
        TextView titleTextView = menuView.findViewById(R.id.menu_item_ticker);
        titleTextView.setText(mTicker);

        mAddStockImageView = menuView.findViewById(R.id.menu_item_add);
        if (mWatchlistTickers.contains(mTicker)) {
            mDrawableId = R.drawable.ic_action_checked_white;
        }
        else {
            mDrawableId = R.drawable.ic_action_add_white;
        }
        mAddStockImageView.setImageDrawable(getResources().getDrawable(mDrawableId));

        mAddStockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawableId == R.drawable.ic_action_add_white) {
                    mDrawableId = R.drawable.ic_action_checked_white;
                    mWatchlistTickers.add(mTicker);
                }
                else {
                    mDrawableId = R.drawable.ic_action_add_white;
                    mWatchlistTickers.remove(mTicker);
                }
                mAddStockImageView.setImageDrawable(getResources().getDrawable(mDrawableId));
            }
        });
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {

        private int mLayoutId;

        private TextView mTickerTextView;
        private TextView mCompanyNameTextView;
        private TextView mPriceTextView;
        private TextView mPriceChangeTextView;

        private SparkView mGraphSparkView;

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

        private RecyclerHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
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

            else if (mLayoutId == R.layout.list_item_stock_detail_graph) {
                mGraphSparkView = itemView.findViewById(R.id.graph_spark_view);

                if (mChartPrices.get(mTicker) != null && mPreviousPrices.get(mTicker) != null) {
                    List<Float> prices = mChartPrices.get(mTicker);
                    Float previousClose = mPreviousPrices.get(mTicker);

                    MySparkAdapter mySparkAdapter = new MySparkAdapter(prices, previousClose);
                    mGraphSparkView.setAdapter(mySparkAdapter);
                    mGraphSparkView.setLineColor(getResources().getColor(mySparkAdapter.getColorId()));
                    mGraphSparkView.getBaseLinePaint().setPathEffect(mySparkAdapter.getDottedBaseline());
                }
            }

            else if (mLayoutId == R.layout.list_item_stock_detail_key_stats) {
                mOpenTextView = itemView.findViewById(R.id.open_price);
                mOpenTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getOpen()));

                mHighTextView = itemView.findViewById(R.id.high_price);
                mHighTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getHighToday()));

                mLowTextView = itemView.findViewById(R.id.low_price);
                mLowTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getLowToday()));

                m52WeekHighTextView = itemView.findViewById(R.id.year_high);
                m52WeekHighTextView.setText(String.format(Locale.US, "%.2f", mStockItem.get52WeekHigh()));

                m52WeekLowTextView = itemView.findViewById(R.id.year_low);
                m52WeekLowTextView.setText(String.format(Locale.US, "%.2f", mStockItem.get52WeekLow()));

                mVolumeTextView = itemView.findViewById(R.id.volume);
                mVolumeTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getVolume()));

                mAvgVolumeTextView = itemView.findViewById(R.id.average_volume);
                mAvgVolumeTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getAvgVolume()));

                mMarketCapTextView = itemView.findViewById(R.id.market_cap);
                mMarketCapTextView.setText(new NumberFormatHelper().formatBigNumber(mStockItem.getMarketCap()));

                mBetaTextView = itemView.findViewById(R.id.beta);
                mBetaTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getBeta()));

                mEpsTextView = itemView.findViewById(R.id.eps);
                mEpsDateTextView = itemView.findViewById(R.id.eps_date);
                if (mStockItem.getLatestEPS() != 0) {
                    mEpsTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getLatestEPS()));
                    mEpsDateTextView.setText(mStockItem.getLatestEPSDate());
                }

                mDividendYieldTextView = itemView.findViewById(R.id.dividend_yield);
                mDividendYieldTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getDividendYield()));

                mPriceEarningTextView = itemView.findViewById(R.id.price_earning);
                if (mStockItem.getPERatio() != 0) {
                    mPriceEarningTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getPERatio()));
                }

                mPriceToBookTextView = itemView.findViewById(R.id.price_to_book);
                if (mStockItem.getPriceToBook() != 0) {
                    mPriceToBookTextView.setText(String.format(Locale.US, "%.2f", mStockItem.getPriceToBook()));
                }
            }

            else if (mLayoutId == R.layout.list_item_stock_detail_company_detail) {
                mSectorTextView = itemView.findViewById(R.id.sector);
                if (!mStockItem.getSector().equals("")) {
                    mSectorTextView.setText(mStockItem.getSector());
                }

                mIndustryTextView = itemView.findViewById(R.id.industry);
                if (!mStockItem.getIndustry().equals("")) {
                    mIndustryTextView.setText(mStockItem.getIndustry());
                }

                mCEOTextView = itemView.findViewById(R.id.CEO);
                if (!mStockItem.getCEO().equals("")) {
                    mCEOTextView.setText(mStockItem.getCEO());
                }

                mExchangeTextView = itemView.findViewById(R.id.exchange);
                mExchangeTextView.setText(mStockItem.getExchange());

                mDescriptionTextView = itemView.findViewById(R.id.description);
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

    private class FetchDataChartTask extends AsyncTask<String, Void, List<Float>> {
        /*
        Fetch prices to display chart
         */
        private String mTicker;

        @Override
        protected List<Float> doInBackground(String... strings) {
            mTicker = strings[0];
            return new DataFetch().fetchChartDataOneDay(mTicker);
        }

        @Override
        protected void onPostExecute(List<Float> prices) {
            mChartPrices.put(mTicker, prices);
            setupAdapter();
        }
    }

    private class FetchPreviousPriceTask extends AsyncTask<String, Void, Float> {
        /*
        Fetch close price of previous day
         */
        private String mTicker;

        @Override
        protected Float doInBackground(String... strings) {
            mTicker = strings[0];
            return new DataFetch().fetchPreviousClose(mTicker);
        }

        @Override
        protected void onPostExecute(Float previousPrice) {
            mPreviousPrices.put(mTicker, previousPrice);
            setupAdapter();
        }
    }
}
