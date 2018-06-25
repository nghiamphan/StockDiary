package com.nphan.android.stockdiary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StockSearchFragment extends Fragment {

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
                List<StockItem> items = new ArrayList<>();
                String query = newText.toLowerCase();
                for (int i = 0; i < mPersistedStockList.size(); i++) {
                    String ticker = mPersistedStockList.get(i).getTicker().toLowerCase();
                    String name = mPersistedStockList.get(i).getCompanyName().toLowerCase();
                    if (ticker.contains(query) || name.contains(query)) {
                        items.add(mPersistedStockList.get(i));
                    }
                }
                mStockItems = items;
                Log.i("HIHI size", Integer.toString(mStockItems.size()));
                setupAdapter();
                return true;
            }
        });
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

        TextView tickerTextView;
        TextView companyNameTextView;

        public StockItemHolder(View itemView) {
            super(itemView);
            tickerTextView = itemView.findViewById(R.id.stock_ticker);
            companyNameTextView = itemView.findViewById(R.id.company_name);
        }

        public void bindItem(StockItem stockItem) {
            tickerTextView.setText(stockItem.getTicker());
            companyNameTextView.setText(stockItem.getCompanyName());
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
