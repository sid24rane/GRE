package com.example.maitr.gre.Word_Detail;

import java.util.ArrayList;

/**
 * Created by sid24rane on 29/10/17.
 */

//https://www.vocabulary.com/lists/194479
public class Word {
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<String> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(ArrayList<String> antonyms) {
        this.antonyms = antonyms;
    }

    private String term;
    private String definition;
    private String sentence;
    private ArrayList<String> synonyms;
    private ArrayList<String> antonyms;

}
