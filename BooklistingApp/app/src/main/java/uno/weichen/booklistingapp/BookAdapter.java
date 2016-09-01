package uno.weichen.booklistingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by weichen on 8/30/16.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> book) {
        super(context, 0, book);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                R.layout.book_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.book_thumbnail);
        if (imageView != null) {
            if (!currentBook.getmImageLink().matches("")) {
                new ImageDownloaderTask(imageView).execute(currentBook.getmImageLink());
            } else {
                imageView.setImageResource(R.drawable.no_thumb);
            }
        }
        TextView title = (TextView) listItemView.findViewById(R.id.book_title);
        title.setText(currentBook.getmTitle());
        TextView subtitle = (TextView) listItemView.findViewById(R.id.book_subtitle);
        subtitle.setText(currentBook.getmSubTitle());
        TextView firstAuthor = (TextView) listItemView.findViewById(R.id.author);
        firstAuthor.setText(currentBook.getmFirstAuthor());
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        date.setText(currentBook.getmPublishDate());
        return listItemView;
    }


    public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("BookAdapter_doInB", "start downloadingBitMap");
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.v("downloadBitmap", "onPost");
            if (isCancelled()) {

                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            Log.v("downloadBitmap", url);
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Log.v("downloadBitmap", "input is not null");
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

}
