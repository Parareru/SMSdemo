package com.example.MessageManager;

/**
 * Created by Lkx on 2014/12/24.
 */
public class Message {
    public boolean isLeft;
    private String content;
    private int _id;

    public Message(boolean isLeft, String content){
        this.isLeft = isLeft;
        this.content = content;
    }

    public void setId(int _id){
        this._id = _id;
    }

    public String getContent(){
        return content;
    }

    public boolean getIsLeft(){
        return isLeft;
    }

    public int getId(){
        return _id;
    }
}
