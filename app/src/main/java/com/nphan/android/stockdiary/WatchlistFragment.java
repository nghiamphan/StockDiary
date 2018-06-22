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

public class WatchlistFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public static WatchlistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WatchlistFragment fragment = new WatchlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private class StockItemHolder extends RecyclerView.ViewHolder {
        private TextView stockTickerTextView;
        private TextView stockPriceTextView;

        public StockItemHolder(View itemView) {
            super(itemView);
            stockTickerTextView = itemView.findViewById(R.id.stock_ticker);
            stockPriceTextView = itemView.findViewById(R.id.stock_price);
        }

        public void bindItem() {

        }
    }
}
