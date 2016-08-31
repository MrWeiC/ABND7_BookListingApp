package uno.weichen.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView title = (TextView) listItemView.findViewById(R.id.book_title);
        title.setText(currentBook.getmTitle());
        TextView subtitle = (TextView) listItemView.findViewById(R.id.book_subtitle);
        subtitle.setText(currentBook.getmSubTitle());
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        date.setText(currentBook.getmPublishDate());
        return listItemView;
    }
}
