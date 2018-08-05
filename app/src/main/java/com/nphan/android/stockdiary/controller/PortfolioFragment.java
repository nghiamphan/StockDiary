package com.nphan.android.stockdiary.controller;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.helper.MySparkAdapter;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSingleton;
import com.nphan.android.stockdiary.model.TradeItem;
import com.nphan.android.stockdiary.model.TradeSingleton;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PortfolioFragment extends Fragment {

    private final static int MILLIS_IN_A_DAY = 1000 * 3600 * 24;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    private TradeSingleton mTradeSingleton;
    private List<String> mPortfolioTickers;
    private List<StockItem> mPortfolioStockItems;

    private HashMap<String, List<Float>> mChartPrices;
    private HashMap<String, Float> mPreviousPrices;
    private HashMap<String, HashMap<Long, Float>> mHistoryChartPrices;

    public static PortfolioFragment newInstance() {

        Bundle args = new Bundle();

        PortfolioFragment fragment = new PortfolioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTradeSingleton = TradeSingleton.get(getActivity());
        mPortfolioTickers = mTradeSingleton.getAllPortfolioTickers();

        mChartPrices = StockSingleton.get(getActivity()).getChartPrices();
        mPreviousPrices = StockSingleton.get(getActivity()).getPreviousPrices();
        mHistoryChartPrices = StockSingleton.get(getActivity()).getHistoryChartPrices();
        FetchTask();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_with_search_item, menu);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.portfolio));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search_icon:
                Intent intent = StockSearchActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPortfolioTickers = mTradeSingleton.getAllPortfolioTickers();
        mHistoryChartPrices = StockSingleton.get(getActivity()).getHistoryChartPrices();
        FetchTask();
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

    private void FetchTask() {
        new FetchPortfolioLastPriceTask().execute();

        for (String ticker : mPortfolioTickers) {
            if (!mPreviousPrices.containsKey(ticker) || mPreviousPrices.get(ticker) == null) {
                new FetchDataChartTask().execute(ticker);
            }

            if (!mChartPrices.containsKey(ticker) || mChartPrices.get(ticker) == null) {
                new FetchPreviousPriceTask().execute(ticker);
            }

            if (!mHistoryChartPrices.containsKey(ticker) || mHistoryChartPrices.get(ticker) == null) {
                new FetchChartDataWithDateTask().execute(ticker);
            }
        }
    }

    private class PositionRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTickerTextView;
        private TextView mSharesTextView;
        private Button mStockPriceButton;
        private SparkView mGraphSparkView;

        private PositionRecyclerHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.list_item_portfolio_position_each_item, parent, false));
            itemView.setOnClickListener(this);
        }

        private void bind(StockItem item) {
            String ticker = item.getTicker();

            mTickerTextView = itemView.findViewById(R.id.stock_ticker);
            mTickerTextView.setText(ticker);

            mSharesTextView = itemView.findViewById(R.id.shares);
            int shares = mTradeSingleton.numberOfSharesByTicker(ticker);
            mSharesTextView.setText(String.format(Locale.US, "%d SHARES", shares));

            mStockPriceButton = itemView.findViewById(R.id.stock_price);
            if (item.getPrice() == null) {
                mStockPriceButton.setText(R.string.not_available);
            }
            else {
                mStockPriceButton.setText(String.format(Locale.US, "%.2f", item.getPrice()));
            }
            mStockPriceButton.setBackgroundColor(getResources().getColor(R.color.grey));

            mGraphSparkView = itemView.findViewById(R.id.graph_spark_view);
            if (mChartPrices.get(ticker) != null && mPreviousPrices.get(ticker) != null) {
                List<Float> prices = mChartPrices.get(ticker);
                Float previousClose = mPreviousPrices.get(ticker);

                MySparkAdapter mySparkAdapter = new MySparkAdapter(prices, previousClose);
                mGraphSparkView.setAdapter(mySparkAdapter);
                mGraphSparkView.setLineColor(getResources().getColor(mySparkAdapter.getColorId()));
                mGraphSparkView.getBaseLinePaint().setPathEffect(mySparkAdapter.getDottedBaseline());

                mStockPriceButton.setBackgroundColor(getResources().getColor(mySparkAdapter.getColorId()));
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = StockDetailActivity.newIntent(getActivity(), (String) mTickerTextView.getText());
            startActivity(intent);
        }
    }

    private class PositionRecyclerAdapter extends RecyclerView.Adapter<PositionRecyclerHolder> {
        private List<StockItem> mPortfolioItems;

        private PositionRecyclerAdapter(List<StockItem> items) {
            mPortfolioItems = items;
        }

        @NonNull
        @Override
        public PositionRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PositionRecyclerHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PositionRecyclerHolder holder, int position) {
            StockItem item = mPortfolioItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            if (mPortfolioItems != null) {
                return mPortfolioItems.size();
            }
            else {
                return 0;
            }
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {
        private int mLayoutId;

        private TextView mTotalEquityTextView;
        private TextView mTodayChangeTextView;
        private SparkView mGraphSparkView;
        private RecyclerView mPositionRecyclerView;

        private RecyclerHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            super(inflater.inflate(layoutId, parent, false));
            mLayoutId = layoutId;
        }

        private void bind() {
            List<Float> equities = processHistoryPrices();

            if (mLayoutId == R.layout.list_item_portfolio_title) {
                mTotalEquityTextView = itemView.findViewById(R.id.total_equity);
                if (equities != null) {
                    Spannable spannableString = new SpannableString(String.format(Locale.US,"$%.2f", equities.get(equities.size()-1)));
                    spannableString.setSpan(new RelativeSizeSpan(0.7f), 0 , 1, 0);
                    mTotalEquityTextView.setText(spannableString);
                }

                mTodayChangeTextView = itemView.findViewById(R.id.today_change);
                if (equities != null && equities.size() >= 2) {
                    Float last = equities.get(equities.size()-1);
                    Float preLast = equities.get(equities.size()-2);
                    Float changeToday = last - preLast;
                    Float percentChange = changeToday / preLast * 100;

                    String changePrefix;
                    if (changeToday < 0) {
                        changePrefix = "-";
                    }
                    else {
                        changePrefix = "+";
                    }
                    mTodayChangeTextView.setText(String.format(Locale.US, changePrefix + "$%.2f" + "  " + "(%.2f%%) TODAY", Math.abs(changeToday), percentChange));
                    if (changeToday < 0) {
                        mTodayChangeTextView.setTextColor(getResources().getColor(R.color.red));
                    }
                    else {
                        mTodayChangeTextView.setTextColor(getResources().getColor(R.color.green));
                    }
                }

            }

            else if (mLayoutId == R.layout.list_item_stock_detail_graph) {
                mGraphSparkView = itemView.findViewById(R.id.graph_spark_view);

                if (equities != null) {
                    MySparkAdapter mySparkAdapter = new MySparkAdapter(equities, equities.get(0));
                    mGraphSparkView.setAdapter(mySparkAdapter);
                    mGraphSparkView.setLineColor(getResources().getColor(mySparkAdapter.getColorId()));
                    mGraphSparkView.getBaseLinePaint().setPathEffect(mySparkAdapter.getDottedBaseline());
                }
            }

            else if (mLayoutId == R.layout.fragment_recycler_view) {
                mPositionRecyclerView = itemView.findViewById(R.id.recycler_view);
                mPositionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                PositionRecyclerAdapter adapter = new PositionRecyclerAdapter(mPortfolioStockItems);
                mPositionRecyclerView.setAdapter(adapter);

            }
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
        int[] mLayoutIdList = {
                R.layout.list_item_portfolio_title,
                R.layout.list_item_stock_detail_graph,
                R.layout.fragment_recycler_view
        };

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
            return mLayoutIdList.length;
        }

        @Override
        public int getItemViewType(int position) {
            return mLayoutIdList[position];
        }
    }

    private List<Float> processHistoryPrices() {
        for (String ticker : mPortfolioTickers) {
            if (!mHistoryChartPrices.containsKey(ticker)) {
                return null;
            }
        }

        List<TradeItem> tradeItems = mTradeSingleton.getTradesSortedByDate();
        List<Float> equities = new ArrayList<>();

        Calendar today = GregorianCalendar.getInstance();
        long timestamp = today.getTimeInMillis();

        int index = 0;
        Calendar curProcessedDay = tradeItems.get(index).getCalendar();
        HashMap<String, Integer> holdings = new HashMap<>();
        while (curProcessedDay.getTimeInMillis() < timestamp){

            while (index < tradeItems.size() && tradeItems.get(index).getCalendar().getTimeInMillis() <= curProcessedDay.getTimeInMillis()) {
                TradeItem item = tradeItems.get(index);
                if (!holdings.containsKey(item.getTicker())) {
                    holdings.put(item.getTicker(), 0);
                }

                Integer shares = holdings.get(item.getTicker());
                if (item.getBuyOrSell().equals("BUY")) {
                    shares += item.getQuantity();
                }
                else {
                    shares -= item.getQuantity();
                }
                holdings.put(item.getTicker(), shares);
                index += 1;
            }

            Float equity = (float) 0;
            for (String ticker : holdings.keySet()) {
                if (mHistoryChartPrices.get(ticker).containsKey(curProcessedDay.getTimeInMillis())) {
                    equity += holdings.get(ticker) * mHistoryChartPrices.get(ticker).get(curProcessedDay.getTimeInMillis());
                }
            }

            if (equity != 0) {
                equities.add(equity);
            }
            curProcessedDay.setTimeInMillis(curProcessedDay.getTimeInMillis() + MILLIS_IN_A_DAY);
        }

        return equities;
    }

    private class FetchPortfolioLastPriceTask extends AsyncTask<Void, Void, List<StockItem>> {
        /*
        Fetch last price of all stocks in portfolios
         */
        @Override
        protected List<StockItem> doInBackground(Void... voids) {
            return new DataFetch().fetchCompanyNameAndPrice(mPortfolioTickers);
        }

        @Override
        protected void onPostExecute(List<StockItem> items) {
            mPortfolioStockItems = items;
            setupAdapter();
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

    private class FetchChartDataWithDateTask extends AsyncTask<String, Void, HashMap<Long, Float>> {
    /*
    Fetch price and date within 5 years
     */
    private String mTicker;

        @Override
        protected HashMap<Long, Float> doInBackground(String... strings) {
            mTicker = strings[0];
            return new DataFetch().fetchChartDataWithDate(mTicker);
        }

        @Override
        protected void onPostExecute(HashMap<Long, Float> fiveYearPrices) {
            mHistoryChartPrices.put(mTicker, fiveYearPrices);
            setupAdapter();
        }
    }
}
