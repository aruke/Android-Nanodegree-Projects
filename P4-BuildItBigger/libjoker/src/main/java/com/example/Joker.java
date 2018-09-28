package com.example;

import java.util.Random;

public class Joker {

    private static JokeStore mJokeStore;

    static {
        mJokeStore = new JokeStore();
    }

    public static String getJoke(){
        Random random = new Random();
        int i = random.nextInt(mJokeStore.getCount());
        return mJokeStore.getJoke(i);
    }
}
