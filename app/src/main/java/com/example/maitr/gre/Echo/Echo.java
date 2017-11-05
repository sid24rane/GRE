package com.example.maitr.gre.Echo;

import java.util.ArrayList;

/**
 * Created by sid24rane on 5/11/17.
 */

public class Echo {
    private String word;
    private String word_id;
    private ArrayList<String> users;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}
