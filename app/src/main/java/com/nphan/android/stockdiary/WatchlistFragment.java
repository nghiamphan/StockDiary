package com.nphan.android.stockdiary;

import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WatchlistFragment extends Fragment {

    private List<String> mWatchlistTickers = new ArrayList<>();
    private List<StockItem> mStockItems = new ArrayList<>();
    private HashMap<String, List> mPrices = new HashMap<>();
    private HashMap<String, Float> mPreviousPrices = new HashMap<>();

    private RecyclerView mRecyclerView;
    private StockItemAdapter mAdapter;

    public static WatchlistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WatchlistFragment fragment = new WatchlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        FetchTask();
    }

    private void FetchTask() {
        mWatchlistTickers = StockSharedPreferences.getTickerWatchlist(getActivity());
        new FetchWatchlistTask().execute();
        for (String ticker : mWatchlistTickers) {
            new FetchDataChartTask().execute(ticker);
            new FetchPreviousPriceTask().execute(ticker);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_watchlist, menu);
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
        FetchTask();
    }

    private void setupAdapter() {
        if (isAdded()) {
            if (mAdapter == null) {
                mAdapter = new StockItemAdapter(mStockItems);
                mRecyclerView.setAdapter(mAdapter);
            }
            else {
                mAdapter.setItems(mStockItems);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class StockItemHolder extends RecyclerView.ViewHolder {
        private TextView mStockTickerTextView;
        private Button mStockPriceButton;
        private SparkView mGraphSparkView;

        public StockItemHolder(View itemView) {
            super(itemView);
            mStockTickerTextView = itemView.findViewById(R.id.stock_ticker);
            mStockPriceButton = itemView.findViewById(R.id.stock_price);
            mGraphSparkView = itemView.findViewById(R.id.graph_spark_view);
        }

        public void bindItem(StockItem stockItem) {
            String ticker = stockItem.getTicker();
            mStockTickerTextView.setText(ticker);

            if (stockItem.getPrice() == null) {
                mStockPriceButton.setText(R.string.not_available);
            }
            else {
                mStockPriceButton.setText(Float.toString(stockItem.getPrice()));
            }
            mStockPriceButton.setBackgroundColor(getResources().getColor(R.color.grey));

            if (mPrices.get(ticker) != null && mPreviousPrices.get(ticker) != null) {
                List<Float> prices = mPrices.get(ticker);
                mGraphSparkView.setAdapter(new MySparkAdapter(ticker, prices));

                int colorId = R.color.green;
                if (prices.get(prices.size()-1) < mPreviousPrices.get(ticker)) {
                    colorId = R.color.red;
                }
                mGraphSparkView.setLineColor(getResources().getColor(colorId));
                mStockPriceButton.setBackgroundColor(getResources().getColor(colorId));

                // Dotted baseline
                Paint baseLinePaint = mGraphSparkView.getBaseLinePaint();
                DashPathEffect dashPathEffect = new DashPathEffect(new float[] {5, 10}, 0);
                baseLinePaint.setPathEffect(dashPathEffect);
            }
        }
    }

    private class StockItemAdapter extends RecyclerView.Adapter<StockItemHolder> {
        private List<StockItem> mItems;

        public StockItemAdapter(List<StockItem> stockItems) {
            mItems = stockItems;
        }

        public void setItems(List<StockItem> stockItems) {
            mItems = stockItems;
        }

        @NonNull
        @Override
        public StockItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(R.layout.list_item_stock_watchlist, parent, false);
            return new StockItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull StockItemHolder holder, int position) {
            StockItem stockItem = mItems.get(position);
            holder.bindItem(stockItem);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class FetchWatchlistTask extends AsyncTask<Void, Void, List<StockItem>> {
        /*
        Fetch ticker and last price
         */
        @Override
        protected List<StockItem> doInBackground(Void... voids) {
            return new DataFetch().fetchStockItem(mWatchlistTickers);
        }

        @Override
        protected void onPostExecute(List<StockItem> items) {
            mStockItems = items;
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
            mPrices.put(mTicker, prices);
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

    public class MySparkAdapter extends SparkAdapter {
        private String mTicker;
        private List<Float> mPrices;

        public MySparkAdapter(String ticker, List<Float> prices) {
            mTicker = ticker;
            mPrices = prices;
        }

        @Override
        public int getCount() {
            return mPrices.size();
        }

        @Override
        public Object getItem(int index) {
            return mPrices.get(index);
        }

        @Override
        public float getY(int index) {
            if (mPrices.get(index) == null) {
                return 0;
            }
            return mPrices.get(index);
        }

        @Override
        public float getBaseLine() {
            if (mPreviousPrices.get(mTicker) == null) {
                return 0;
            }
            return mPreviousPrices.get(mTicker);
        }

        @Override
        public boolean hasBaseLine() {
            return getBaseLine() != 0;
        }
    }
}
