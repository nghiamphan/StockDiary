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
import android.widget.Button;
import android.widget.TextView;

import com.nphan.android.stockdiary.DataFetch;
import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.helper.MySparkAdapter;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSingleton;
import com.nphan.android.stockdiary.model.TradeSingleton;
import com.robinhood.spark.SparkView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PortfolioFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;

    private TradeSingleton mTradeSingleton;
    private List<String> mPortfolioTickers;
    private List<StockItem> mPortfolioStockItems;

    private HashMap<String, List<Float>> mChartPrices;
    private HashMap<String, Float> mPreviousPrices;

    public static PortfolioFragment newInstance() {

        Bundle args = new Bundle();

        PortfolioFragment fragment = new PortfolioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTradeSingleton = TradeSingleton.get(getActivity());
        mPortfolioTickers = mTradeSingleton.getAllPortfolioTickers();

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

    private void FetchTask() {
        new FetchPortfolioLastPriceTask().execute();

        for (String ticker : mPortfolioTickers) {
            if (!mPreviousPrices.containsKey(ticker) || mPreviousPrices.get(ticker) == null) {
                new FetchDataChartTask().execute(ticker);
            }

            if (!mChartPrices.containsKey(ticker) || mChartPrices.get(ticker) == null) {
                new FetchPreviousPriceTask().execute(ticker);
            }
        }
    }

    private class PositionRecyclerHolder extends RecyclerView.ViewHolder {

        private TextView mTickerTextView;
        private TextView mSharesTextView;
        private Button mStockPriceButton;
        private SparkView mGraphSparkView;

        private PositionRecyclerHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.list_item_portfolio_position_each_item, parent, false));
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

        private RecyclerView mPositionRecyclerView;

        private RecyclerHolder(LayoutInflater inflater, ViewGroup parent, int layoutId) {
            super(inflater.inflate(layoutId, parent, false));
            mLayoutId = layoutId;
        }

        private void bind() {
            if (mLayoutId == R.layout.fragment_recycler_view) {
                mPositionRecyclerView = itemView.findViewById(R.id.recycler_view);
                mPositionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                PositionRecyclerAdapter adapter = new PositionRecyclerAdapter(mPortfolioStockItems);
                mPositionRecyclerView.setAdapter(adapter);

            }
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
        int[] mLayoutIdList = {
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
}
