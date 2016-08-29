package uno.weichen.booklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log message
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * URL to query the GOOGLE BOOK API dataset for book information
     */
    final private String GOOGLE_BOOK_API_BASE_URL =
        "https://www.googleapis.com/books/v1/volumes?q=";
    final private String FILTER = "&maxResults=5";

    private String googleBookApiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText searchBookTitle = (EditText) findViewById(R.id.bookname_edittext);


        Button searchBookButton = (Button) findViewById(R.id.search_book_button);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("MainActivity", "start the Async task");
                //update url
                googleBookApiUrl = GOOGLE_BOOK_API_BASE_URL+ searchBookTitle.getText()+FILTER;
                Log.d(LOG_TAG,googleBookApiUrl);

                SearchAsyncTask task = new SearchAsyncTask();
                task.execute();
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

    public class SearchAsyncTask extends AsyncTask<URL, Void, Book> {
        @Override
        protected Book doInBackground(URL... urls) {
            //Create an instance of URL
            URL url = createUrl(googleBookApiUrl);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                Log.d(LOG_TAG, "jsonResponse is:" + jsonResponse);
            } catch (IOException e) {
                Log.e(LOG_TAG, " Something wrong to make the Http Request", e);
            }
            Book book = extractBookFromJason(jsonResponse);
            Log.d(LOG_TAG,book.toString());
            return null;
        }
    }

    private Book extractBookFromJason(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            if (itemsArray.length() > 0) {
                JSONObject book = itemsArray.getJSONObject(0);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String subTitle = volumeInfo.getString("subtitle");
                String firstAuthor = volumeInfo.getJSONArray("authors").getString(0);
                String publishDate = volumeInfo.getString("publishedDate");
                String imageLink = volumeInfo.getJSONObject("imageLinks").getString
                    ("smallThumbnail");
                String previewLink = volumeInfo.getString("previewLink");

                return new Book(title, subTitle, firstAuthor, publishDate, imageLink, previewLink);
            }


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
