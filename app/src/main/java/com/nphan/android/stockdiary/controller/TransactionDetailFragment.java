package com.nphan.android.stockdiary.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nphan.android.stockdiary.R;
import com.nphan.android.stockdiary.model.TradeItem;
import com.nphan.android.stockdiary.model.TradeSingleton;

import java.util.UUID;

public class TransactionDetailFragment extends Fragment {

    private final static String ARG_ID = "arg_id";
    private final static String ARG_TICKER = "arg_ticker";

    private UUID mId;
    private String mTicker;
    private TradeItem mTradeItem;
    private boolean mCreatedNew;

    private TextView mTickerTextView;
    private RadioGroup mBuyOrSellRadioGroup;
    private RadioButton mBuyRadioButton;
    private RadioButton mSellRadioButton;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private Button mUpdateButton;
    private Button mDeleteButton;

    public static TransactionDetailFragment newInstance(String ticker) {

        Bundle args = new Bundle();
        args.putString(ARG_TICKER, ticker);

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TransactionDetailFragment newInstance(UUID id, String ticker) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, id);
        args.putString(ARG_TICKER, ticker);

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mId = (UUID) getArguments().getSerializable(ARG_ID);
        mTicker = getArguments().getString(ARG_TICKER);
        if (mId != null) {
            mTradeItem = TradeSingleton.get(getActivity()).getTradeById(mId);
            mCreatedNew = false;
        }
        else {
            mTradeItem = new TradeItem();
            mTradeItem.setTicker(mTicker);
            mCreatedNew = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container, false);

        final TradeSingleton tradeSingleton = TradeSingleton.get(getActivity());

        mTickerTextView = view.findViewById(R.id.ticker);
        mTickerTextView.setText(mTicker);

        mUpdateButton = view.findViewById(R.id.update_button);
        mUpdateButton.setEnabled(false);
        checkUpdateButton();

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCreatedNew) {
                    tradeSingleton.addTrade(mTradeItem);
                }
                else {
                    tradeSingleton.updateTrade(mTradeItem);
                }

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        mDeleteButton = view.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCreatedNew) {
                    tradeSingleton.deleteTrade(mTradeItem);
                }

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        mBuyOrSellRadioGroup = view.findViewById(R.id.buy_or_sell_radio_group);
        mBuyRadioButton = view.findViewById(R.id.buy_radio_button);
        mSellRadioButton = view.findViewById(R.id.sell_radio_button);
        mBuyOrSellRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.buy_radio_button) {
                    mTradeItem.setBuyOrSell("BUY");
                }
                else {
                    mTradeItem.setBuyOrSell("SELL");
                }
            }
        });

        mQuantityEditText = view.findViewById(R.id.quantity);
        mQuantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTradeItem.setQuantity(Integer.valueOf(s.toString()));
                checkUpdateButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPriceEditText = view.findViewById(R.id.price);
        mPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTradeItem.setPrice(Float.valueOf(s.toString()));
                checkUpdateButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!mCreatedNew) {
            if (mTradeItem.getBuyOrSell() == "SELL") {
                mSellRadioButton.setChecked(true);
            }
            mQuantityEditText.setText(String.valueOf(mTradeItem.getQuantity()));
            mPriceEditText.setText(String.valueOf(mTradeItem.getPrice()));
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getResources().getString(R.string.enter_transaction));
        }
    }

    private void checkUpdateButton() {
        if (mTradeItem.getQuantity() != 0 && mTradeItem.getPrice() != null) {
            mUpdateButton.setEnabled(true);
        }
    }
}