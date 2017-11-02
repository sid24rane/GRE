package com.example.maitr.gre.Word_Detail;

/**
 * Created by sid24rane on 3/11/17.
 */

public class TodayWord{

    private String word;
    private String id;

    public TodayWord(String word, String id) {
        this.word = word;
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}