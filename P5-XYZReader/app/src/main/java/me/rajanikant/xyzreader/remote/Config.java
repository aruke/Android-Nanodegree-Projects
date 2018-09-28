package me.rajanikant.xyzreader.remote;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;

    static {
        URL url = null;
        try {
            // This URL is not working anymore. So I am changing it to static contents of another Nanodegree Student
            // url = new URL("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/data.json" );

            url = new URL("https://raw.githubusercontent.com/Protino/dump/master/Lego/data.json" );
        } catch (MalformedURLException ignored) {
            // TODO: throw a real error
        }

        BASE_URL = url;
    }
}
