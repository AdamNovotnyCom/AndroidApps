package com.example.jokes;

import java.util.Random;

public class JokesMain {
    String[] jokes = {
            "Headmaster: I've had complaints about you, Johnny, from all your teachers. What have you been doing?\n\nJohnny: Nothing, sir.\n\nHeadmaster: Exactly.",
            "If you think it's hard to meet new people, pick up the wrong golf ball on the course sometime.",
            "A wife walked into the bedroom and found her husband in bed with his golf clubs.\n\nSeeing the astonished look on her face, he calmly said, 'Well, you said I had to choose, right?'",
            "SO why does the golfer carry two shirts? In case he gets a hole in one",
            "How many Dallas Cowboys does it take to change a tire?\n\nOne, unless it's a blowout, in which case they all show up",
            "How do football players spend the first week of training camp?\n\nStudying the Miranda Rights",
            "What's the difference between a hockey game and a boxing match?\n\nIn a hockey game, the fights are real",
            "What's the difference between the Detroit Lions and a dollar bill?\n\nYou can still get four quarters out of a dollar bill."
    };

    public String getJoke() {
        // generate random joke
        Random rand = new Random();
        int min = 0;
        int max = jokes.length;
        int randomIdx = rand.nextInt(max - min) + min;

        return jokes[randomIdx];
    }
}
