package com.example.rahulraina.rottenpotatoes;

import org.junit.Test;
import junit.framework.Assert;
import java.util.ArrayList;

/**
 * Created by aniruddha on 4/6/16.
 */
public class MovieTests {

    Movie one = new Movie("one", "", "", "", "");
    Movie two = new Movie ("one", "", "", "", "");
    Movie three = new Movie ("three", "", "", "", "");

    @Test
    public void testEquals() {

        Assert.assertFalse(one.equals(null));
        Assert.assertFalse(one.equals(new Object()));
        Assert.assertTrue(one.equals(one));
        Assert.assertFalse(one.equals(three));
        Assert.assertTrue(one.equals(two));
    }

}
