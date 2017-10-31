package com.example.maitr.gre.Word_Detail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maitr.gre.R;

public class WordDetailActivity extends AppCompatActivity {
    private TextView word, meaning, synonym, antonym;
    private ImageView sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        word = (TextView) findViewById(R.id.word);
        meaning = (TextView) findViewById(R.id.meaning);
        synonym = (TextView) findViewById(R.id.synonym);
        antonym = (TextView) findViewById(R.id.antonym);
        sound = (ImageView) findViewById(R.id.pronounce);

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
