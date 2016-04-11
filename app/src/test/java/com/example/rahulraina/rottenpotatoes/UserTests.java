package com.example.rahulraina.rottenpotatoes;

import org.junit.Test;
import junit.framework.Assert;
import java.util.ArrayList;

/**
 * Created by aniruddha on 4/6/16.
 */
public class UserTests {

    User one = new User("one", "", "", "");
    User two = new User("one", "", "", "");
    User three = new User("three", "", "", "");

    @Test
    public void testEquals() {

        Assert.assertFalse(one.equals(null));
        Assert.assertFalse(one.equals(new Object()));
        Assert.assertTrue(one.equals(one));
        Assert.assertFalse(one.equals(three));
        Assert.assertTrue(one.equals(two));
    }

}
