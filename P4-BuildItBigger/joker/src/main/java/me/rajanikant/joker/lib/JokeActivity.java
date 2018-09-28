package me.rajanikant.joker.lib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

    public static final String INTENT_TELL_A_JOKE = "me.rajanikant.joker.lib.TELL_A_JOKE";
    public static final String EXTRA_STRING_JOKE = "joke";
    public static final String EXTRA_STRING_TITLE = "title";
    public static final String EXTRA_BOOLEAN_SET_UP_BUTTON_ENABLED = "up-button";

    TextView textContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        textContents = (TextView) findViewById(R.id.activity_joke_contents);

        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            // Set joke to Screen
            String joke = sourceIntent.getStringExtra(EXTRA_STRING_JOKE);
            if (joke != null && !TextUtils.isEmpty(joke)) {
                textContents.setText(joke);
            }

            // Set ActionBar
            ActionBar actionBar = getSupportActionBar();

            String title = sourceIntent.getStringExtra(EXTRA_STRING_TITLE);
            if (title != null && !TextUtils.isEmpty(title) && actionBar != null) {
                actionBar.setTitle(title);
            }

            Boolean upEnabled = sourceIntent.getBooleanExtra(
                    EXTRA_BOOLEAN_SET_UP_BUTTON_ENABLED, false);
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(upEnabled);
            }
        }

        textContents.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
