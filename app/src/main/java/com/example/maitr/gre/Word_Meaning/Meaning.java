package com.example.maitr.gre.Word_Meaning;

import java.util.ArrayList;

/**
 * Created by sid24rane on 1/11/17.
 */

public class Meaning {

    private String word;
    private String a;
    private String b;
    private String c;
    private String d;
    private String answer;
    private String id;

    public String getMeaningid() {
        return meaningid;
    }

    public void setMeaningid(String meaningid) {
        this.meaningid = meaningid;
    }

    private String meaningid;

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    private ArrayList<String> users = new ArrayList<>();

    Meaning(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }



}
