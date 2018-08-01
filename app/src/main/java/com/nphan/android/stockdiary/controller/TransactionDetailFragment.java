package com.nphan.android.stockdiary.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nphan.android.stockdiary.R;

import java.util.UUID;

public class TransactionDetailFragment extends Fragment {

    private final static String ARG_TICKER = "arg_ticker";

    public static TransactionDetailFragment newInstance(String ticker) {

        Bundle args = new Bundle();
        args.putString(ARG_TICKER, ticker);

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TransactionDetailFragment newInstance(UUID id, String ticker) {

        Bundle args = new Bundle();
        args.putString(ARG_TICKER, ticker);

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);

        return view;
    }
}
