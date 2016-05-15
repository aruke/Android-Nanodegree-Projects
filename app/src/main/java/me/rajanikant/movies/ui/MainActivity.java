package me.rajanikant.movies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;
import me.rajanikant.movies.R;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    @InjectView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_main_tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.activity_main_view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view_on_github) {
            Uri link = Uri.parse(getString(R.string.project_github_link));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, link);
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }else{
                Toast.makeText(this, "No App found to resolve content", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieItemClicked(Movie movie) {
        Toast.makeText(MainActivity.this, "Movie item clicked with title " + movie.getTitle(), Toast.LENGTH_SHORT).show();
        Intent detailIntent = new Intent(this, MovieDetailsActivity.class);
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_TITLE, movie.getTitle());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW, movie.getOverview());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RATINGS, movie.getVoteAverage());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH, movie.getPosterPath());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        startActivity(detailIntent);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            fragmentList.add(MovieGridFragment.newInstance(Constants.MOVIE_TAG_TOP_RATED));
            fragmentTitleList.add(getString(R.string.movie_tag_top_rated));
            fragmentList.add(MovieGridFragment.newInstance(Constants.MOVIE_TAG_POPULAR));
            fragmentTitleList.add(getString(R.string.movie_tag_popular));
            fragmentList.add(FavMovieGridFragment.newInstance());
            fragmentTitleList.add(getString(R.string.movie_tag_favourite));
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
