package com.example.jokes;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class unitTests {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testJoke1() {
        JokesMain jm = new JokesMain();
        assert jm.getJoke().length() > 2;
    }

    @Test
    public void testJoke2() {
        JokesMain jm = new JokesMain();
        assert jm.getJoke() instanceof String;
    }
}