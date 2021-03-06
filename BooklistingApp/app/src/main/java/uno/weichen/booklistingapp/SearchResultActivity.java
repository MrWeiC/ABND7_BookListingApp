package uno.weichen.booklistingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by weichen on 8/31/16.
 */
public class SearchResultActivity extends AppCompatActivity {

    /**
     * Tag for the log message
     */
    public static final String LOG_TAG = SearchResultActivity.class.getSimpleName();


    /**
     * Array list to store the books from the json query
     */
    public ArrayList<Book> books = new ArrayList<>();
    public ListView listView;
    public BookAdapter bookAdapter;

    /**
     * URL to query the GOOGLE BOOK API dataset for book information
     */
    final private String GOOGLE_BOOK_API_BASE_URL =
        "https://www.googleapis.com/books/v1/volumes?q=";
    final private String FILTER = "&maxResults=20";

    private String googleBookApiUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);
        /**
         * Get the searchKey from the MainActivity
         */
        Intent intent = getIntent();
        String searchKey = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.d("Search", "The passed searchKey value is: " + searchKey);
        Log.d("Search", "start the Async task");

        /**
         * Update the url
         */
        try {
            searchKey = URLEncoder.encode(searchKey, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Encoding search Key error", e);
        }

        googleBookApiUrl = GOOGLE_BOOK_API_BASE_URL + searchKey +
            FILTER;
        Log.d(LOG_TAG, googleBookApiUrl);


        /**
         * Start the AsyncTask for the query Arraylist<Book> books
         */
        SearchAsyncTask task = new SearchAsyncTask();
        task.execute();

        // set the adapter the book list
        bookAdapter = new BookAdapter(this, books);
        //bundle this adapter to the list view
        listView = (ListView) findViewById(R.id.list);
        //set the list view with the bookAdpater
        listView.setAdapter(bookAdapter);


        /**
         * click the list item, the user will go to the preview link if it has one
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book) parent.getItemAtPosition(position);
                String url = book.getmPreviewLink();
                if(!url.matches("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    public class SearchAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            //Create an instance of URL
            URL url = createUrl(googleBookApiUrl);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                Log.d(LOG_TAG, "jsonResponse is:" + jsonResponse);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Something wrong to make the Http Request", e);
            }
            ArrayList<Book> books = extractBookFromJason(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (bookAdapter == null || books == null) {
                //locate the NODATA TextView
                TextView nodataTextview = (TextView)findViewById(R.id.nodata_textview);
                nodataTextview.setVisibility(View.VISIBLE);
                return;
            }
            updateUi(books);
        }
    }

    /**
     * To update ui in the asynctask
     * @param booksList
     */
    private void updateUi(ArrayList<Book> booksList) {
        bookAdapter.clear();
        bookAdapter.addAll(booksList);
        bookAdapter.notifyDataSetChanged();
        listView.setAdapter(bookAdapter);
    }


    private ArrayList<Book> extractBookFromJason(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            ArrayList<Book> books = new ArrayList<>();

            for (int i = 0; itemsArray.length() > i; i++) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                //try to get data from json, allow empty object will pass as ""
                String title = volumeInfo.getString("title");
                String subTitle = "";
                if (volumeInfo.has("subtitle")) {
                    subTitle = volumeInfo.getString("subtitle");
                }
                /**
                 * Get the first author
                 */
                String firstAuthor = "";
                if (volumeInfo.has("authors")) {
                    firstAuthor = volumeInfo.getJSONArray("authors").getString(0);
                }
                /**
                 * Only get the year
                 */
                String publishDate = "";
                if (volumeInfo.has("publishedDate")) {
                    publishDate = volumeInfo.getString("publishedDate");
                    if (publishDate.length() > 5) {
                        publishDate = publishDate.substring(0, 4);
                    }
                }
                String imageLink = "";
                if (volumeInfo.has("imageLinks")) {
                    imageLink = volumeInfo.getJSONObject("imageLinks").getString
                        ("smallThumbnail");
                }
                String previewLink = "";
                if (volumeInfo.has("previewLink")) {
                    previewLink = volumeInfo.getString("previewLink");
                }
                books.add(new Book(title, subTitle, firstAuthor, publishDate, imageLink,
                    previewLink));
            }
            return books;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        return null;
    }


    private String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();//might got IOException
                jsonResponse = readFromStream(inputStream);
                Log.d(LOG_TAG, "Successfully get json.");

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Status code:" + urlConnection.getResponseCode(), e);
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the
     * server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                .forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
