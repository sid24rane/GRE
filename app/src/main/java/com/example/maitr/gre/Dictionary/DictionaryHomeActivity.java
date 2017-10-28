package com.example.maitr.gre.Dictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.maitr.gre.R;

public class DictionaryHomeActivity extends AppCompatActivity {

    private CardView l1, l2, l3, l4, l5, l6, l7, l8, l9, l10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_home);

        l1 = (CardView) findViewById(R.id.level1);
        l2 = (CardView) findViewById(R.id.level2);
        l3 = (CardView) findViewById(R.id.level3);
        l4 = (CardView) findViewById(R.id.level4);
        l5 = (CardView) findViewById(R.id.level5);
        l6 = (CardView) findViewById(R.id.level6);
        l7 = (CardView) findViewById(R.id.level7);
        l8 = (CardView) findViewById(R.id.level8);
        l9 = (CardView) findViewById(R.id.level9);
        l10= (CardView) findViewById(R.id.level10);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });

        l10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
            }
        });


    }
}
