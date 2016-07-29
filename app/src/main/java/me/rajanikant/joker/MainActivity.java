package me.rajanikant.joker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.rajanikant.joker.lib.JokeActivity;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.content_main_button_tell_joke)
    Button buttonTellJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.content_main_button_tell_joke)
    public void onButtonClick() {
        Intent intent = new Intent(JokeActivity.INTENT_TELL_A_JOKE);
        intent.putExtra(JokeActivity.EXTRA_STRING_JOKE, "He he eh !");
        intent.putExtra(JokeActivity.EXTRA_BOOLEAN_SET_UP_BUTTON_ENABLED, true);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Something went wrong. Joker is not here!", Toast.LENGTH_SHORT).show();
        }
    }
}
