package com.czettner.bookingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ResultsActivityLog";

    private ListView listView;
    private ArrayList<Book> books = new ArrayList<Book>();
    BookAdapter itemsAdapter;
    ProgressBar progressBar;
    TextView nodataText;
    private static final String QUERY_URL = "https://www.googleapis.com/books/v1/volumes?q=%s&maxResults=20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        listView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        nodataText = (TextView) findViewById(R.id.nodata);
        itemsAdapter = new BookAdapter(this, books);
        listView.setAdapter(itemsAdapter);
        progressBar.setVisibility(View.VISIBLE);
        nodataText.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = books.get(position).getInfoLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        Intent intent = getIntent();

        String searchString = intent.getStringExtra(MainActivity.EXTRA_SEARCH_STRING);

        if (!searchString.isEmpty()) {
            new JSONParseAsync().execute(String.format(QUERY_URL, searchString));
        }
    }

    /**
     * Update the screen.
     */
    private void updateUi(JSONObject jsonObject) {
        // Display the results in the UI
        Log.d(LOG_TAG, jsonObject.toString());
        books = jsonToBookArray(jsonObject);
        itemsAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        if (books.size() == 0) {
            nodataText.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Book> jsonToBookArray(JSONObject jsonObject) {
        books.clear();
        try {
            JSONObject book;
            JSONObject volumeInfo;
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            Log.d(LOG_TAG, "LENGTH::: " + itemsArray.length());
            for (int i=0; i < itemsArray.length(); i++) {
                book = itemsArray.getJSONObject(i);
                volumeInfo = book.getJSONObject("volumeInfo");
                String title = "";
                String subtitle = "";
                String author = "";

                if (volumeInfo.has("title")) {
                    title = volumeInfo.getString("title");
                }
                if (volumeInfo.has("subtitle")) {
                    subtitle = volumeInfo.getString("subtitle");
                }
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    // TODO too many nested loops and conditions, refactor this to a method
                    for (int j=0; j < authors.length(); j++) {
                        author += authors.getString(j);
                        if (j < authors.length() - 1) {
                            author += ", ";
                        }
                    }
                }
                Book tmpBook = new Book(title, subtitle, author);
                if (volumeInfo.has("infoLink")) {
                    tmpBook.setInfoLink(volumeInfo.getString("infoLink"));
                }
                // Add book
                books.add(tmpBook);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException " + e.getMessage());
        }

        return books;
    }

    /**
     * Asynctask to retrieve and process JSON from Google API.
     */
    private class JSONParseAsync extends AsyncTask<String, Void, JSONObject> {

        private static final String LOG_TAG = "Async";

        @Override
        protected JSONObject doInBackground(String... urls) {
            // params comes from the execute() call: urls[0] is the url.
            Log.d(LOG_TAG, urls[0]);
            try {
                return downloadJSON(urls[0]);
            } catch (IOException e) {
                Log.d(LOG_TAG, "Unable to retrieve JSON. URL may be invalid.");
            }

            return null;
        }

        /**
         * Given a URL, establishes an HttpUrlConnection and retrieves the JSON content as a
         * InputStream, then it returns as a JSONObject.
         *
         * @param myUrl url to call
         * @return JSONObject for further processing
         * @throws IOException
         */
        private JSONObject downloadJSON(String myUrl) throws IOException {
            InputStream is = null;

            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(LOG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                return readFromStream(is);

                // Closing the InputStream after we finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        /**
         *
         * @param is InputStream object
         * @return
         */
        private JSONObject readFromStream(InputStream is) {
            try {
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr);
                }

                return new JSONObject(responseStrBuilder.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //if something went wrong, return null
            return null;
        }

        /**
         * Update the screen with the returned books data (which was the result of the
         * {@link ResultsActivity}).
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) {
                return;
            }

            updateUi(jsonObject);
        }

    }
}
