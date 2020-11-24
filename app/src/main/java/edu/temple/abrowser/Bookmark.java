package edu.temple.abrowser;

import android.net.Uri;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;

public class Bookmark implements Serializable {

    private String title;
    private String url;


    public Bookmark()
    {
        this.title = "Google";
        this.url = "http://www.google.com";
    }

    public Bookmark(String u)
    {
        Uri x = Uri.parse(u);
        this.title = x.getPath();
        this.url = u;
    }

    public Bookmark(String t, String u)
    {
        this.url = u;
        this.title = t;
    }


    public String getTitle()
    {
        return this.title;
    }

    public String getUrl()
    {
        return this.url;
    }




}