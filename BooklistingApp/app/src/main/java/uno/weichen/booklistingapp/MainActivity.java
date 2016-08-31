package uno.weichen.booklistingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
public final static String EXTRA_MESSAGE = "searchKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText searchBookTitle = (EditText) findViewById(R.id.bookname_edittext);


        final Button searchBookButton = (Button) findViewById(R.id.search_book_button);
        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBookTitle.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a key word for a " +
                        "search", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Start a new activity to display the result in the search.
                Intent searchIntent = new Intent(MainActivity.this,SearchResultActivity.class);
                searchIntent.putExtra(EXTRA_MESSAGE,searchBookTitle.getText().toString());
                startActivity(searchIntent);


            }

        });


    }






}
