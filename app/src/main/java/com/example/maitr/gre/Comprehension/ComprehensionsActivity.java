package com.example.maitr.gre.Comprehension;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.maitr.gre.R;

public class ComprehensionsActivity extends AppCompatActivity {

    private TextView comprehension, optionA, optionB, optionC, optionD, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensions);

        comprehension = (TextView) findViewById(R.id.comprehensionQuestion);
        optionA = (TextView) findViewById(R.id.compOptionA);
        optionB = (TextView) findViewById(R.id.compOptionB);
        optionC = (TextView) findViewById(R.id.compOptionC);
        optionD = (TextView) findViewById(R.id.compOptionD);
        answer = (TextView) findViewById(R.id.comprehensionCorrectAnswer);

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
