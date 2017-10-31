package com.example.maitr.gre.Word_Meaning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.maitr.gre.R;

public class MeaningActivity extends AppCompatActivity {

    private TextView meaningWord, optionA, optionB, optionC, optionD, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        meaningWord = (TextView) findViewById(R.id.meaningWord);
        optionA = (TextView) findViewById(R.id.optionA);
        optionB = (TextView) findViewById(R.id.optionB);
        optionC = (TextView) findViewById(R.id.optionC);
        optionD = (TextView) findViewById(R.id.optionD);
        answer = (TextView) findViewById(R.id.meaningCorrectAnswer);

        answer.setVisibility(View.INVISIBLE);

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
