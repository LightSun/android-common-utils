package com.android.volley.extra.util;

import java.util.ArrayList;
import java.util.List;

public class Pairs {
    List<Pair> mPairs = new ArrayList<>(3);

    public Pairs with(String name, String value) {
        mPairs.add(new Pair(name, value));
        return this;
    }

    public List<Pair> getPairs() {
        return mPairs;
    }

    public static class Pair {
        String name;
        String value;

        public Pair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
