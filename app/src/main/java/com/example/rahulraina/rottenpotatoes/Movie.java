package com.example.rahulraina.rottenpotatoes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aniruddha on 3/2/16.
 */
public class Movie {

    private Map<String, Float> ratings = new HashMap<>();
    private Map<String, Integer> numRatings = new HashMap<>();
    private String title;
    private String year;
    private String rated;
    private String released;
    private String runtime;
    float averageRating = -1.0f;

    public Movie(String title, String year, String rated, String released, String runtime) {
        this.title = title;
        this.year = year;
        this.rated = rated;
        this.released = released;
        this.runtime = runtime;
    }

    public void addRating(String major, float rating) {
        if (ratings.keySet().contains(major)) {
            int num = numRatings.get(major);
            float currentRating = ratings.get(major);
            float newRating = ((currentRating * num) + rating) / (num + 1);
            ratings.put(major, newRating);
            numRatings.put(major, num + 1);
        } else {
            ratings.put(major, rating);
            numRatings.put(major, 1);
        }

        int totalRatings = 0;
        float sumRatings = 0;
        for (String eachMajor: ratings.keySet()) {
            totalRatings += numRatings.get(eachMajor);
            sumRatings += numRatings.get(eachMajor) * ratings.get(eachMajor);
        }

        averageRating = sumRatings / totalRatings;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public float getRatingByMajor(String major) {
        return ratings.get(major);
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRated() {
        return rated;
    }

    public String getReleased() {
        return released;
    }

    public String getRuntime() {
        return runtime;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Movie)) {
            return false;
        }
        if (other == this) {
            return true;
        }

        Movie that = (Movie) (other);

        return title.equals(that.title)
                && year.equals(that.year)
                && rated.equals(that.rated)
                && released.equals(that.released)
                && runtime.equals(that.runtime);
    }
}
