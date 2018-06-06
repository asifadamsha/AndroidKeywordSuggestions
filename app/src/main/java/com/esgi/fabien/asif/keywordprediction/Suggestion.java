package com.esgi.fabien.asif.keywordprediction;

import android.support.annotation.NonNull;

public class Suggestion {

    private String word;
    private int occurance;

    public Suggestion(String word, int occurance) {
        this.word = word;
        this.occurance = occurance;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getOccurance() {
        return occurance;
    }

    public void setOccurance(int occurance) {
        this.occurance = occurance;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "word='" + word + '\'' +
                ", occurance=" + occurance +
                '}';
    }
}
