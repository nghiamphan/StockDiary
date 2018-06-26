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
    private static final String CHART = "chart";
    private static final String ONE_DAY = "1d";

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

    public List<StockItem> fetchStockTickerAndName() {

        List<StockItem> stockItems = new ArrayList<>();

        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(REF_DATA)
                    .appendPath(SYMBOLS);

            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            parseStockTickerAndName(stockItems, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return stockItems;
    }

    private void parseStockTickerAndName(List<StockItem> stockItems, String  jsonString) throws JSONException{
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject stockJsonObject = jsonArray.getJSONObject(i);
            StockItem stockItem = new StockItem();
            stockItem.setTicker(stockJsonObject.getString("symbol"));
            stockItem.setCompanyName(stockJsonObject.getString("name"));
            stockItems.add(stockItem);
        }
    }

    public List<StockItem> fetchStockItem(List<String> tickers) {

        List<StockItem> stockItems = new ArrayList<>();

        for (String ticker : tickers) {
            try {
                Uri.Builder uriBuilder = ENDPOINT
                        .buildUpon()
                        .appendPath(STOCK)
                        .appendPath(ticker)
                        .appendPath(QUOTE);
                String urlString = uriBuilder.toString();
                String jsonString = getUrlString(urlString);
                parseStockItem(stockItems, jsonString, ticker);
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

    private void parseStockItem(List<StockItem> stockItems, String jsonString, String ticker) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonString);
        StockItem item = new StockItem();
        item.setTicker(ticker);
        item.setCompanyName(jsonObject.getString("companyName"));

        if (jsonObject.isNull("latestPrice")) {
            item.setPrice(null);
        }
        else {
            item.setPrice(Float.valueOf(jsonObject.getString("latestPrice")));
        }

        stockItems.add(item);
    }

    private List<Float> fetchChartData(String ticker, String period) {
        List<Float> prices = new ArrayList<>();
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(CHART)
                    .appendPath(period);
            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            parseChartData(prices, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return prices;
    }

    public List<Float> fetchChartDataOneDay(String ticker) {
        return fetchChartData(ticker, ONE_DAY);
    }

    private void parseChartData(List<Float> prices, String jsonString) throws JSONException {
        JSONArray dataJsonArray = new JSONArray(jsonString);
        Float price = null;
        Float firstPrice = new Float(0);
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject data = dataJsonArray.getJSONObject(i);
            if (data.has("close")) {
                if (price == null) {
                    firstPrice = Float.valueOf(data.getString("close"));
                }
                price = Float.valueOf(data.getString("close"));
            }
            prices.add(price);
        }

        /*
        If the first few values in "prices" are null, set them equal to the first non-null value
         */
        for (int i = 0; i < prices.size(); i++) {
            if (prices.get(i) == null) {
                prices.set(i, firstPrice);
            }
            else {
                continue;
            }
        }
    }
}
