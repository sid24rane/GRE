package com.example.maitr.gre.Jumbled_Words;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maitr.gre.R;

public class JumbledWordsActivity extends AppCompatActivity {

    private TextView jumbledWord;
    private TextView hint;
    private EditText answer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        jumbledWord = (TextView) findViewById(R.id.jumbledWord);
        hint = (TextView) findViewById(R.id.hintMeaning);
        answer = (EditText) findViewById(R.id.jumbledAnswer);
    }
}
