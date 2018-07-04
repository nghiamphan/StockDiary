package com.nphan.android.stockdiary.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.StockItem;
import com.nphan.android.stockdiary.model.StockSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class StockSearchFragment extends Fragment {

    /*
    mTickers retrieves the list of watchlist tickers, adds and removes tickers based on user's action, and is saved back to SharedPreferences once the Fragment is paused.
    Note for why mTickers is saved to SharedPreferences in method onPause: Order of calls:
    StockSearchActivity.onPause()
    WatchlistActivity.onResume()
    ...
    StockSearchActivity.onStop()
    StockSearchActivity.onDestroy()

    mStockItems is the list of stocks to be displayed in RecyclerView.

    mPersistedStockList is the list of all stocks (with tickers and names).
     */
    private List<String> mTickers;
    private List<StockItem> mStockItems;
    private List<StockItem> mPersistedStockList;

    private RecyclerView mRecyclerView;
    private StockItemAdapter mAdapter;

    public static StockSearchFragment newInstance() {

        Bundle args = new Bundle();

        StockSearchFragment fragment = new StockSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPersistedStockList = StockSharedPreferences.getStockList(getActivity());
        mTickers = StockSharedPreferences.getTickerWatchlist(getActivity());
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
        inflater.inflate(R.menu.fragment_stock_search, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search_view);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    mStockItems = searchString(newText.toUpperCase());
                }
                else {
                    mStockItems = new ArrayList<>();
                }
                setupAdapter();
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().finish();
                return false;
            }
        });
    }

    private List<StockItem> searchString(String query) {
        /*
        Search stock based on ticker and name, and return stocks based on relevance
         */
        List<StockItem> items = new ArrayList<>();
        for (int i = 0; i < mPersistedStockList.size(); i++) {
            StockItem stockItem = mPersistedStockList.get(i);
            String ticker = stockItem.getTicker();
            String name = stockItem.getCompanyName().toUpperCase();
            if (!items.contains(stockItem)) {
                if (ticker.equals(query)) {
                    items.add(stockItem);
                }
                else if (query.length() == 2 && ticker.length() >= 2 && ticker.charAt(0) == query.charAt(0) && ticker.charAt(1) == query.charAt(1))
                {
                    items.add(stockItem);
                }
                else if (query.length() == 1 && ticker.charAt(0) == query.charAt(0)) {
                    items.add(stockItem);
                }
                else if (name.equals(query)) {
                    items.add(stockItem);
                }
            }
        }

        for (int i = 0; i < mPersistedStockList.size(); i++) {
            StockItem stockItem = mPersistedStockList.get(i);
            String ticker = stockItem.getTicker();
            if (!items.contains(stockItem) && ticker.contains(query)) {
                items.add(stockItem);
            }
        }
        for (int i = 0; i < mPersistedStockList.size(); i++) {
            StockItem stockItem = mPersistedStockList.get(i);
            String name = stockItem.getCompanyName().toUpperCase();
            if (!items.contains(stockItem) && name.contains(query)) {
                items.add(stockItem);
            }
        }
        return items;
    }

    @Override
    public void onPause() {
        super.onPause();
        StockSharedPreferences.setTickerWatchlist(getActivity(), mTickers);
    }

    public void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = new StockItemAdapter(mStockItems);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setItems(mStockItems);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class StockItemHolder extends RecyclerView.ViewHolder {

        private StockItem mStockItem;

        TextView mTickerTextView;
        TextView mCompanyNameTextView;
        ImageButton mAddStockImageButton;
        private int mDrawableRsId;

        public StockItemHolder(View itemView) {
            super(itemView);
            mTickerTextView = itemView.findViewById(R.id.stock_ticker);
            mCompanyNameTextView = itemView.findViewById(R.id.company_name);
            mAddStockImageButton = itemView.findViewById(R.id.add_stock_image_button);
            mAddStockImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawableRsId == R.drawable.ic_action_add) {
                        mDrawableRsId = R.drawable.ic_action_checked;
                        mTickers.add(mStockItem.getTicker());
                    }
                    else {
                        mDrawableRsId = R.drawable.ic_action_add;
                        mTickers.remove(mStockItem.getTicker());
                    }
                    mAddStockImageButton.setImageDrawable(getResources().getDrawable(mDrawableRsId));
                }
            });
        }

        public void bindItem(StockItem stockItem) {
            mStockItem = stockItem;
            mTickerTextView.setText(stockItem.getTicker());
            mCompanyNameTextView.setText(stockItem.getCompanyName());
            if (mTickers.contains(mStockItem.getTicker())) {
                mDrawableRsId = R.drawable.ic_action_checked;
            }
            else {
                mDrawableRsId = R.drawable.ic_action_add;
            }
            mAddStockImageButton.setImageDrawable(getResources().getDrawable(mDrawableRsId));
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.list_item_stock_search, parent, false);
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
}
