package com.nphan.android.stockdiary;

import android.net.Uri;
import android.util.Log;

import com.nphan.android.stockdiary.model.StockItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class DataFetch {

    private static final String TAG = "DataFetch";
    private static final String REF_DATA = "ref-data";
    private static final String STOCK = "stock";
    private static final String SYMBOLS = "symbols";
    private static final String QUOTE = "quote";
    private static final String PREVIOUS = "previous";
    private static final String CHART = "chart";
    private static final String ONE_DAY = "1d";
    private static final String ONE_MONTH = "1m";
    private static final String THREE_MONTH = "3m";
    private static final String SIX_MONTH = "6m";
    private static final String YEAR_TO_DATE = "ytd";
    private static final String ONE_YEAR = "1y";
    private static final String TWO_YEAR = "2y";
    private static final String FIVE_YEAR = "5y";
    private static final String COMPANY = "company";
    private static final String STATS = "stats";

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

    public List<StockItem> fetchCompanyNameAndPrice(List<String> tickers) {
        /*
        Given a list of tickers, get company names and prices.
         */

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
                parseCompanyNameAndPrice(stockItems, jsonString, ticker);
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

    private void parseCompanyNameAndPrice(List<StockItem> stockItems, String jsonString, String ticker) throws JSONException{
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
        /*
        Given a ticker and period, get that ticker's stock price in that time period
         */

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
        Float firstPrice = (float) 0;
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject data = dataJsonArray.getJSONObject(i);
            if (data.has("marketClose")) {
                if (price == null) {
                    firstPrice = Float.valueOf(data.getString("marketClose"));
                }
                price = Float.valueOf(data.getString("marketClose"));
            }
            else if (data.has("close")) {
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

    public Float fetchPreviousClose(String ticker) {
        /*
        Fetch previous closed price
         */

        Float previousPrice = null;
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(PREVIOUS);
            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            JSONObject jsonObject = new JSONObject(jsonString);
            previousPrice = Float.valueOf(jsonObject.getString("close"));
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return previousPrice;
    }

    public StockItem fetchCompanyInfo(String ticker) {
        /*
        Fetch company information: company name, sector, industry, CEO, description
         */

        StockItem stockItem = new StockItem();
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(COMPANY);
            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            parseCompanyInfo(stockItem, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return stockItem;
    }

    private void parseCompanyInfo(StockItem stockItem, String jsonString) throws JSONException{
        JSONObject companyObject = new JSONObject(jsonString);
        stockItem.setCompanyName(companyObject.getString("companyName"));
        stockItem.setSector(companyObject.getString("sector"));
        stockItem.setIndustry(companyObject.getString("industry"));
        stockItem.setCEO(companyObject.getString("CEO"));
        stockItem.setExchange(companyObject.getString("exchange"));
        stockItem.setDescription(companyObject.getString("description"));
    }

    public StockItem fetchKeyStats(String ticker) {
        /*
        Fetch key stats: 52w high, 52w low, beta, eps, eps date, dividend yield, price to book
         */

        StockItem stockItem = new StockItem();
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(STATS);
            String uriString = uriBuilder.toString();
            String jsonString = getUrlString(uriString);
            parseKeyStats(stockItem, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return stockItem;
    }

    private void parseKeyStats(StockItem stockItem, String jsonString) throws JSONException {
        JSONObject companyObject = new JSONObject(jsonString);
        stockItem.set52WeekHigh(Float.valueOf(companyObject.getString("week52high")));
        stockItem.set52WeekLow(Float.valueOf(companyObject.getString("week52low")));
        stockItem.setBeta(Float.valueOf(companyObject.getString("beta")));
        stockItem.setLatestEPS(Float.valueOf(companyObject.getString("latestEPS")));
        stockItem.setLatestEPSDate(companyObject.getString("latestEPSDate"));
        stockItem.setDividendYield(Float.valueOf(companyObject.getString("dividendYield")));
        if (!companyObject.isNull("priceToBook")) {
            stockItem.setPriceToBook(Float.valueOf(companyObject.getString("priceToBook")));
        }
    }

    public StockItem fetchStockQuote(String ticker) {
        /*
        Fetch key stats: open, high, low, volume, avg volume, market cap, pe ratio, price, change, change percent
         */

        StockItem stockItem = new StockItem();
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(QUOTE);
            String uriString = uriBuilder.toString();
            String jsonString = getUrlString(uriString);
            parseStockQuote(stockItem, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return stockItem;
    }

    private void parseStockQuote(StockItem stockItem, String jsonString) throws JSONException {
        JSONObject companyObject = new JSONObject(jsonString);

        stockItem.setPrice(Float.valueOf(companyObject.getString("latestPrice")));
        stockItem.setChangeToday(Float.valueOf(companyObject.getString("change")));
        stockItem.setChangePercent(Float.valueOf(companyObject.getString("changePercent")));

        stockItem.setOpen(Float.valueOf(companyObject.getString("open")));
        stockItem.setHighToday(Float.valueOf(companyObject.getString("high")));
        stockItem.setLowToday(Float.valueOf(companyObject.getString("low")));
        stockItem.setVolume(Float.valueOf(companyObject.getString("latestVolume")));
        stockItem.setAvgVolume(Float.valueOf(companyObject.getString("avgTotalVolume")));
        stockItem.setMarketCap(Float.valueOf(companyObject.getString("marketCap")));
        if (!companyObject.isNull("peRatio")) {
            stockItem.setPERatio(Float.valueOf(companyObject.getString("peRatio")));
        }
    }

    private HashMap<Calendar, Float> fetchChartDataWithDate(String ticker) {
        /*
        Given a ticker, get that ticker's stock price in 5-year period
         */

        HashMap<Calendar, Float> pricesByDate = new HashMap<>();
        try {
            Uri.Builder uriBuilder = ENDPOINT
                    .buildUpon()
                    .appendPath(STOCK)
                    .appendPath(ticker)
                    .appendPath(CHART)
                    .appendPath(FIVE_YEAR);
            String urlString = uriBuilder.toString();
            String jsonString = getUrlString(urlString);
            parseChartDataWithDate(pricesByDate, jsonString);
        }
        catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return pricesByDate;
    }

    private void parseChartDataWithDate(HashMap<Calendar, Float> pricesByDate, String jsonString) throws JSONException {
        JSONArray dataJsonArray = new JSONArray(jsonString);

        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject data = dataJsonArray.getJSONObject(i);

            String date = data.getString("date");
            String[] partitions = date.split("-");
            int year = Integer.valueOf(partitions[0]);
            int month = Integer.valueOf(partitions[1]);
            int day = Integer.valueOf(partitions[2]);
            Calendar calendar = new GregorianCalendar(year, month, day);
            Float price = Float.valueOf(data.getString("close"));

            pricesByDate.put(calendar, price);
        }
    }
}
