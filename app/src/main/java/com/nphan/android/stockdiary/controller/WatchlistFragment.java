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
import com.nphan.android.stockdiary.model.StockSharedPreferences;
import com.nphan.android.stockdiary.model.StockSingleton;
import com.robinhood.spark.SparkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WatchlistFragment extends Fragment {

    /*
    mStockItems: the list of stocks to be displayed in RecyclerView; each item contains ticker and last price information

    mChartPrices:
    - key: ticker
    - value: list of prices for charting

    mPreviousPrices:
    - key: ticker
    - value: list of prices for previous day
     */

    private List<String> mWatchlistTickers = new ArrayList<>();
    private List<StockItem> mStockItems = new ArrayList<>();
    private HashMap<String, List<Float>> mChartPrices;
    private HashMap<String, Float> mPreviousPrices;

    private RecyclerView mRecyclerView;
    private StockItemAdapter mAdapter;

    public static WatchlistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WatchlistFragment fragment = new WatchlistFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private void FetchTask() {
        mWatchlistTickers = StockSharedPreferences.getTickerWatchlist(getActivity());
        new FetchWatchlistLastPriceTask().execute();

        for (String ticker : mWatchlistTickers) {
            if (!mPreviousPrices.containsKey(ticker) || mPreviousPrices.get(ticker) == null) {
                new FetchDataChartTask().execute(ticker);
            }

            if (!mChartPrices.containsKey(ticker) || mChartPrices.get(ticker) == null) {
                new FetchPreviousPriceTask().execute(ticker);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mChartPrices = StockSingleton.get(getActivity()).getChartPrices();
        mPreviousPrices = StockSingleton.get(getActivity()).getPreviousPrices();
        FetchTask();
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
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.watch_list));
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

    private class StockItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mStockTickerTextView;
        private Button mStockPriceButton;
        private SparkView mGraphSparkView;

        private StockItemHolder(View itemView) {
            super(itemView);
            mStockTickerTextView = itemView.findViewById(R.id.stock_ticker);
            mStockPriceButton = itemView.findViewById(R.id.stock_price);
            mGraphSparkView = itemView.findViewById(R.id.graph_spark_view);
            itemView.setOnClickListener(this);
        }

        public void bindItem(StockItem stockItem) {
            String ticker = stockItem.getTicker();
            mStockTickerTextView.setText(ticker);

            if (stockItem.getPrice() == null) {
                mStockPriceButton.setText(R.string.not_available);
            }
            else {
                mStockPriceButton.setText(String.format(Locale.US, "%.2f", stockItem.getPrice()));
            }
            mStockPriceButton.setBackgroundColor(getResources().getColor(R.color.grey));

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
            Intent intent = StockDetailActivity.newIntent(getActivity(), (String) mStockTickerTextView.getText());
            startActivity(intent);
        }
    }

    private class StockItemAdapter extends RecyclerView.Adapter<StockItemHolder> {
        private List<StockItem> mItems;

        private StockItemAdapter(List<StockItem> stockItems) {
            mItems = stockItems;
        }

        private void setItems(List<StockItem> stockItems) {
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

    private class FetchWatchlistLastPriceTask extends AsyncTask<Void, Void, List<StockItem>> {
        /*
        Fetch ticker and last price
         */
        @Override
        protected List<StockItem> doInBackground(Void... voids) {
            return new DataFetch().fetchCompanyNameAndPrice(mWatchlistTickers);
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
