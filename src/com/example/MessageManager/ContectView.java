package com.example.MessageManager;

import android.widget.ArrayAdapter;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ContectView{
    private String name;
    private String date;
    private String body;
    private int read;

    public ContectView(String name, String date, String body, int read){
        this.body = body;
        this.date = date;
        this.name = name;
        this.read = read;
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

    public int getRead(){
        return read;
    }
}
