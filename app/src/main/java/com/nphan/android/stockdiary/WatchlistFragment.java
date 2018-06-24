package com.nphan.android.stockdiary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WatchlistFragment extends Fragment {

    private List<StockItem> mStockItems = new ArrayList<>();
    private List<String> mTickers = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private StockItemAdapter mAdapter;
    private SearchView mSearchView;

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
        new FetchDataTask().execute();
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

        MenuItem searchItem = menu.findItem(R.id.toolbar_search_item);
        mSearchView = (SearchView) searchItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupAutoSearchView(SearchView searchView) {
        AutoCompleteTextView autoCompleteTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        autoCompleteTextView.setThreshold(1);

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, mTickers);
        autoCompleteTextView.setAdapter(newsAdapter);
    }

    private void setupAdapter() {
        if (isAdded()) {
            if (mAdapter == null) {
                mAdapter = new StockItemAdapter(mStockItems);
                mRecyclerView.setAdapter(mAdapter);
            }
            else {

            }
        }
    }

    private class StockItemHolder extends RecyclerView.ViewHolder {
        private TextView stockTickerTextView;
        private TextView stockPriceTextView;

        public StockItemHolder(View itemView) {
            super(itemView);
            stockTickerTextView = itemView.findViewById(R.id.stock_ticker);
            stockPriceTextView = itemView.findViewById(R.id.stock_price);
        }

        public void bindItem(StockItem stockItem) {
            stockTickerTextView.setText(stockItem.getTicker());

            if (stockItem.getPrice() == null) {
                stockPriceTextView.setText(R.string.not_available);
            }
            else {
                stockPriceTextView.setText(Float.toString(stockItem.getPrice()));
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
            View itemView = inflater.inflate(R.layout.list_item_stock, parent, false);
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

    private class FetchDataTask extends AsyncTask<Void, Void, List<StockItem>> {
        @Override
        protected List<StockItem> doInBackground(Void... voids) {
            mTickers = new DataFetch().fetchStockTickers();
            return new DataFetch().fetchStockQuote(mTickers);
        }

        @Override
        protected void onPostExecute(List<StockItem> items) {
            mStockItems = items;
            setupAdapter();
            setupAutoSearchView(mSearchView);
        }
    }
}
