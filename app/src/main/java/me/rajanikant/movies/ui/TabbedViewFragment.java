package me.rajanikant.movies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;
import me.rajanikant.movies.Utility;

public class TabbedViewFragment extends Fragment {

    @InjectView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_main_tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.activity_main_view_pager)
    ViewPager viewPager;

    public TabbedViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabbed_view, container, false);
        ButterKnife.inject(this, view);

        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

        parentActivity.setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(parentActivity.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (!Utility.isNetworkAvailable(parentActivity)) {

            // If network is not available switched to save movies
            viewPager.setCurrentItem(2, true);

            Snackbar.make(viewPager, R.string.nwtwork_error, Snackbar.LENGTH_SHORT).show();
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view_on_github) {
            Uri link = Uri.parse(getString(R.string.project_github_link));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, link);
            if (webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(webIntent);
            } else {
                Snackbar.make(viewPager, "No App found to resolve content", Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
