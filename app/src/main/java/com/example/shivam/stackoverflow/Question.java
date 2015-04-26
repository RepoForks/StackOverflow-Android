package com.example.shivam.stackoverflow;

/**
 * Created by Shivam on 26/04/15 at 1:21 PM.
 */
public class Question {

    public String title,author,votes;

    public Question()
    {

    }
    public Question(String title,String author,String votes)
    {
        this.title = title;
        this.author = author;
        this.votes = votes;
    }
}
