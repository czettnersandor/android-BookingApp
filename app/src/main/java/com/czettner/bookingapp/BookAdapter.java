package com.czettner.bookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructor
     *
     * @param context Activity
     * @param books Books list
     */
    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;

        // Check if the view is being reused, otherwise create it.
        if (listViewItem == null) {
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleView = (TextView) listViewItem.findViewById(R.id.title);
        TextView subtitleView = (TextView) listViewItem.findViewById(R.id.subtitle);
        TextView authorView = (TextView) listViewItem.findViewById(R.id.author);

        titleView.setText(currentBook.getTitle());
        subtitleView.setText(currentBook.getSubtitle());
        authorView.setText(currentBook.getAuthors());

        return listViewItem;
    }
}
