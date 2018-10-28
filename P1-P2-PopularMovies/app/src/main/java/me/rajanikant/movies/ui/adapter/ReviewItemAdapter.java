package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Review;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 14 Jun 2016
 */
public class ReviewItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Review> reviews;

    public ReviewItemAdapter(Context context, ArrayList<Review> objects) {
        this.context = context;
        reviews = objects;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        }

        Review review = reviews.get(position);

        ((TextView) convertView.findViewById(R.id.item_review_content)).setText(review.getContent());
        ((TextView) convertView.findViewById(R.id.item_review_author)).setText(review.getAuthor());

        return convertView;
    }
}
