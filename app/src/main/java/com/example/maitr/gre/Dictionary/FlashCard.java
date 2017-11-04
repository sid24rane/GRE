package com.example.maitr.gre.Dictionary;

/**
 * Created by sid24rane on 29/10/17.
 */

public class FlashCard {
    private String term;
    private String definition;
    private String sentence;

    public FlashCard(String term, String definition, String sentence) {
        this.term = term;
        this.definition = definition;
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }


    FlashCard(){}



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
}
