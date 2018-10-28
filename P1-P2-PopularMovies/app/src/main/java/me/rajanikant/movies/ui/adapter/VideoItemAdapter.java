package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Video;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 14 Jun 2016
 */
public class VideoItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Video> videos;

    public VideoItemAdapter(Context context, ArrayList<Video> objects) {
        this.context = context;
        this.videos = objects;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public Object getItem(int position) {
        return videos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        }

        Video video = videos.get(position);

        ((TextView) convertView.findViewById(R.id.item_video_title)).setText(video.getName());
        ((TextView) convertView.findViewById(R.id.item_video_site)).setText(video.getSite());
        if (video.getSite().toLowerCase().equals("youtube")){
            Picasso.with(context).load("http://img.youtube.com/vi/" + video.getKey() + "/default.jpg")
                    .into((ImageView)convertView.findViewById(R.id.item_video_thumbnail));
        }

        return convertView;
    }
}
