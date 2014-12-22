package com.example.MessageManager;

import android.widget.ArrayAdapter;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ContectView{
    private String name;
    private String date;
    private String body;

    public ContectView(String name, String date, String body){
        this.body = body;
        this.date = date;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public String getBody(){
        return body;
    }
}
