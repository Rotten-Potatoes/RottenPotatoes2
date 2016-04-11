package com.example.rahulraina.rottenpotatoes;

import org.junit.Test;
import junit.framework.Assert;
import java.util.ArrayList;

/**
 * Created by aniruddha on 4/6/16.
 */
public class RottenPotatoesTests {

    User user = new User("user", "pass", "Bob", "Waters");
    User user2 = new User("user2", "pass2", "Giorgi", "Tkeshelashvili");

    @Test
    public void testAddNewUser() {

        Assert.assertNull(RottenPotatoes.getCurrentUser());

        RottenPotatoes.add(user);
        Assert.assertEquals(user, RottenPotatoes.getCurrentUser());

        RottenPotatoes.add(user2);
        Assert.assertEquals(user, RottenPotatoes.getCurrentUser());

    }

    @Test
    public void testLocalLogin() {
        Assert.assertNull(RottenPotatoes.localLogin("not valid", "pass"));

        Assert.assertEquals(new User("", "", "", ""), RottenPotatoes.localLogin("user", "wrong pass"));

        Assert.assertEquals(user, RottenPotatoes.localLogin("user", "pass"));

    }

    @Test
    public void testSortMovies() {
        Movie one = new Movie("one", "", "", "", "");
        Movie two = new Movie ("two", "", "", "", "");
        Movie three = new Movie ("three", "", "", "", "");

        one.addRating("CS", 5.0f);
        one.addRating("ME", 5.0f);
        two.addRating("CS", 5.0f);
        two.addRating("ME", 1.0f);
        three.addRating("CS", 4.0f);

        ArrayList<Movie> order1 = new ArrayList<>();
        order1.add(two);
        order1.add(three);
        order1.add(one);

        ArrayList<Movie> order2 = new ArrayList<>();
        order2.add(three);
        order2.add(one);
        order2.add(two);

        ArrayList<Movie> order3 = new ArrayList<>();
        order3.add(two);
        order3.add(one);

        ArrayList<Movie> order4 = new ArrayList<>();
        order4.add(one);
        order4.add(two);
        order4.add(three);

        Assert.assertEquals(order1, RottenPotatoes.sortMovies(order4, true, ""));
        Assert.assertNull(RottenPotatoes.sortMovies(order4, false, ""));
        Assert.assertEquals(order2, RottenPotatoes.sortMovies(order4, true, "CS"));
        Assert.assertEquals(order3, RottenPotatoes.sortMovies(order4, true, "ME"));
        Assert.assertEquals(order4, RottenPotatoes.sortMovies(order4, false, "CS"));
        Assert.assertEquals(new ArrayList<Movie>(), RottenPotatoes.sortMovies(order4, true, "AE"));

    }


}
