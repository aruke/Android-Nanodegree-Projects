package me.rajanikant.joker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MainFragment extends Fragment {

    @InjectView(R.id.fragment_main_button_tell_joke)
    Button buttonTellJoke;

    private static JokeApi myApiService;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @OnClick(R.id.fragment_main_button_tell_joke)
    public void onButtonClick() {
        new EndpointsAsyncTask().execute();
    }

    private void startJokeActivity(String joke) {
        Intent intent = new Intent(JokeActivity.INTENT_TELL_A_JOKE);
        intent.putExtra(JokeActivity.EXTRA_STRING_JOKE, joke);
        intent.putExtra(JokeActivity.EXTRA_BOOLEAN_SET_UP_BUTTON_ENABLED, true);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Something went wrong. Joker is not here!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (myApiService == null) {
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

            try {
                return myApiService.tellJoke().execute().getContents();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            startJokeActivity(result);
        }
    }
}
