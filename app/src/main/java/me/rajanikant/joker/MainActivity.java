package me.rajanikant.joker;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.rajanikant.joker.backend.jokeApi.JokeApi;
import me.rajanikant.joker.lib.JokeActivity;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.activity_main_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.content_main_button_tell_joke)
    Button buttonTellJoke;

    private static JokeApi myApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        new EndpointsAsyncTask().execute(this);
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

    class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {

        private Context context;

        @Override
        protected String doInBackground(Context... params) {
            if(myApiService == null) {
                JokeApi.Builder builder = new JokeApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://192.168.43.56:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
            }

            context = params[0];

            try {
                return myApiService.tellJoke().execute().getContents();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            buttonTellJoke.setText(result);
        }
    }
}
