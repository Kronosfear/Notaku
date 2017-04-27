package com.heavyobj.notaku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Card extends AppCompatActivity
{

    private String name;
    private String watched;

    public Card(String name, String watched)
    {
        this.name = name;
        this.watched = watched;
    }

    public String getWatched()
    {
        return watched;
    }

    public String getName()
    {
        return name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
    }
}
