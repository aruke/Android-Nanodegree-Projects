package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Video;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 14 Jun 2016
 */
public class VideoItemAdapter extends ArrayAdapter<Video> {

    public VideoItemAdapter(Context context, List<Video> objects) {
        super(context, R.layout.item_video, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
        }

        Video video = getItem(position);

        ((TextView) convertView.findViewById(R.id.item_video_title)).setText(video.getName());
        ((TextView) convertView.findViewById(R.id.item_video_site)).setText(video.getSite());
        if (video.getSite().toLowerCase().equals("youtube")){
            Picasso.with(getContext()).load("http://img.youtube.com/vi/" + video.getKey() + "/default.jpg")
                    .into((ImageView)convertView.findViewById(R.id.item_video_thumbnail));
        }

        return convertView;
    }
}
