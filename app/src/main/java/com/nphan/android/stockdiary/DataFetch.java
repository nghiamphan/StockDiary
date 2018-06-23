package com.nphan.android.stockdiary;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataFetch {

    private static final String TAG = "DataFetch";
    private static final String REF_DATA = "ref-data";
    private static final String STOCK = "stock";
    private static final String SYMBOLS = "symbols";
    private static final String QUOTE = "quote";

    private static final Uri ENDPOINT = Uri
            .parse("https://api.iextrading.com/1.0")
            .buildUpon()
            .build();

    private byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlString);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlString) throws IOException {
        return new String(getUrlBytes(urlString));
    }

    public List<String> fetchStockTickers() {

        List<String> tickers = new ArrayList<>();

        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(REF_DATA)
                    .appendPath(SYMBOLS);

            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            parseStockTickers(tickers, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return tickers;
    }

    private void parseStockTickers(List<String> tickers, String  jsonString) throws JSONException{
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject stockJsonObject = jsonArray.getJSONObject(i);
            tickers.add(stockJsonObject.getString("symbol"));
        }
    }

    public List<StockItem> fetchStockQuote(List<String> tickers) {

        List<StockItem> stockItems = new ArrayList<>();

        int i = 0;
        for (String ticker : tickers) {
            i++;
            if (i > 100) {
                continue;
            }
            try {
                Uri.Builder uriBuilder = ENDPOINT
                        .buildUpon()
                        .appendPath(STOCK)
                        .appendPath(ticker)
                        .appendPath(QUOTE);
                String urlString = uriBuilder.toString();
                String jsonString = getUrlString(urlString);
                parseStockQuote(stockItems, jsonString, ticker);
                Log.i(TAG, ticker);
            }
            catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch items", ioe);
            }
            catch (JSONException je) {
                Log.e(TAG, "Failed to parse JSON", je);
            }
        }

        return stockItems;
    }

    private void parseStockQuote(List<StockItem> stockItems, String jsonString, String ticker) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonString);
        StockItem item = new StockItem();
        item.setTicker(ticker);

        if (jsonObject.isNull("latestPrice")) {
            item.setPrice(null);
        }
        else {
            item.setPrice(Float.valueOf(jsonObject.getString("latestPrice")));
        }

        stockItems.add(item);
    }
}
