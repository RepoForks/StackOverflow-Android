package com.example.shivam.stackoverflow;

/**
 * Created by Shivam on 26/04/15 at 1:21 PM.
 */
public class Question {

    public String title,author,votes,id;

    public Question()
    {

    }
    public Question(String title,String author,String votes,String id)
    {
        this.title = title;
        this.author = author;
        this.votes = votes;
        this.id = id;
    }

    public String getID()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getVotes()
    {
        return votes;
    }
}
