package com.example.rahulraina.rottenpotatoes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aniruddha on 3/2/16.
 * holds different movies
 */
public class Movie {

    /**
     * Stores average ratings by major
     */
    private Map<String, Float> ratings = new HashMap<>();

    /**
     * Stores number of ratings per major
     */
    private Map<String, Integer> numRatings = new HashMap<>();

    /**
     * movie's title
     */
    private String title;

    /**
     * movie's year
     */
    private String year;

    /**
     * movie's MPAA rating
     */
    private String rated;

    /**
     * Movie's release year
     */
    private String released;

    /**
     * Movie's runtime
     */
    private String runtime;


    /**
     * Movie's avg rating overall
     */
    private float averageRating = -1.0f;

    /**
     * Constructor for holding a movie
     * @param title title of the movie
     * @param year year the movie was released
     * @param rated what the movie was rated (G, PG, etc)
     * @param released what year the movie was released
     * @param runtime the runtime of the movie
     */
    public Movie(String setTitle, String setYear, String setRated, String setReleased, String setRuntime) {
        this.title = setTitle;
        this.year = setYear;
        this.rated = setRated;
        this.released = setReleased;
        this.runtime = setRuntime;
    }

    /**
     * Add rating and average it with all the ratings currently in the system
     * @param major need the major of the person so later we can search via major
     * @param rating the actual rating you want to give the movie
     */
    public void addRating(String major, float rating) {
        RottenPotatoes.addToRated(this);
        if (ratings.keySet().contains(major)) {
            final int num = numRatings.get(major);
            final float currentRating = ratings.get(major);
            final float newRating = ((currentRating * num) + rating) / (num + 1);
            ratings.put(major, newRating);
            numRatings.put(major, num + 1);
        } else {
            ratings.put(major, rating);
            numRatings.put(major, 1);
        }

        int totalRatings = 0;
        float sumRatings = 0;
        for (final String eachMajor: ratings.keySet()) {
            totalRatings += numRatings.get(eachMajor);
            sumRatings += numRatings.get(eachMajor) * ratings.get(eachMajor);
        }

        averageRating = sumRatings / totalRatings;
    }

    /**
     * returns the average rating
     * @return float of the average rating
     */
    public float getAverageRating() {
        return averageRating;
    }

    /**
     * Returns the average mating by the requested major
     * @param major of students whose rating you're curious about
     * @return float the ratings of a movie based on major
     */
    public float getRatingByMajor(String major) {
        return ratings.get(major);
    }

    /**
     * get the title of the movie
     * @return String the title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * get the year the movie was released
     * @return String the year the movie was released
     */
    public String getYear() {
        return year;
    }

    /**
     * get what the movie is rated
     * @return String what the movie is rated
     */
    public String getRated() {
        return rated;
    }

    /**
     * the year the movie was released
     * @return String of when the movie was released
     */
    public String getReleased() {
        return released;
    }

    /**
     * get the runtime of the movie
     * @return String runtime of the movie
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Checks if the movie hsa already been rated by someone before
     * @param other compared to another movie
     * @return boolean of if the movie equals another
     */
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

        final Movie that = (Movie) (other);

        return title.equals(that.title)
                && year.equals(that.year)
                && rated.equals(that.rated)
                && released.equals(that.released)
                && runtime.equals(that.runtime);
    }

    public int hashCode() {
        int result = 55;

        result = 37 * result + title.hashCode();
        result = 37 * result + year.hashCode();
        result = 37 * result + rated.hashCode();
        result = 37 * result + released.hashCode();
        result = 37 * result + runtime.hashCode();

        return result;

    }

    public boolean ratedByMajor(String major) {
        return ratings.keySet().contains(major);
    }
}
