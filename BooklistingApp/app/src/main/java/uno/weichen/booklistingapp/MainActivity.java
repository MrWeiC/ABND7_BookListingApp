package uno.weichen.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                /**
                 * send notification if the user didn't put keyword for the search
                 */
                if (searchBookTitle.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a key word for a " +
                        "search", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 * Send notification if the user didn't connect to network
                 */
                if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "You need to connect to Internet for " +
                        "this search ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Start a new activity to display the result in the search.
                Intent searchIntent = new Intent(MainActivity.this, SearchResultActivity.class);
                searchIntent.putExtra(EXTRA_MESSAGE, searchBookTitle.getText().toString());
                startActivity(searchIntent);
            }

        });
    }

    /**
     * Check the device has network connection.
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
