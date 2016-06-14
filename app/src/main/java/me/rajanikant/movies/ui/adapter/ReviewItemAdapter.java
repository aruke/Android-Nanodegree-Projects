package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Review;
import me.rajanikant.movies.api.model.Video;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 14 Jun 2016
 */
public class ReviewItemAdapter extends ArrayAdapter<Review>{

    public ReviewItemAdapter(Context context, List<Review> objects) {
        super(context, R.layout.item_review, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review, parent, false);
        }

        Review review = getItem(position);

        ((TextView) convertView.findViewById(R.id.item_review_content)).setText(review.getContent());
        ((TextView) convertView.findViewById(R.id.item_review_author)).setText(review.getAuthor());

        return convertView;
    }
}
