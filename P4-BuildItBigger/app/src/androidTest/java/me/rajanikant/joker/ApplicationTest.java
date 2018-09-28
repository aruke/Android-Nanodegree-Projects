package me.rajanikant.joker;

import android.app.Application;
import android.os.AsyncTask;
import android.test.ApplicationTestCase;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import me.rajanikant.joker.backend.jokeApi.JokeApi;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAsyncTask() throws Exception {

        class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                JokeApi myApiService;
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

                try {
                    return myApiService.tellJoke().execute().getContents();
                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                assertNotNull(result);
                assertNotSame(result.length(), 0);
            }
        }

        new EndpointsAsyncTask().execute();
        Thread.sleep(5000);
    }
}