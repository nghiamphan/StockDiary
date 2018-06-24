package com.nphan.android.stockdiary;

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

import java.util.ArrayList;
import java.util.List;

public class StockSearchFragment extends Fragment {

    private List<StockItem> mStockItems = new ArrayList<>();

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
        StockItem s1 = new StockItem();
        s1.setTicker("A");
        s1.setCompanyName("AAA");
        StockItem s2 = new StockItem();
        s2.setTicker("BB");
        s2.setCompanyName("B");
        StockItem s3 = new StockItem();
        s3.setTicker("CCCC");
        s3.setCompanyName("AAAdfwfs");
        mStockItems.add(s1);
        mStockItems.add(s3);
        mStockItems.add(s1);
        mStockItems.add(s2);
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

    public void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = new StockItemAdapter(mStockItems);
            mRecyclerView.setAdapter(mAdapter);
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
            //tickerTextView.setText("AA");
            //companyNameTextView.setText("BBBBB");

        }
    }

    private class StockItemAdapter extends RecyclerView.Adapter<StockItemHolder> {
        private List<StockItem> mItems;

        public StockItemAdapter(List<StockItem> stockItems) {
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
