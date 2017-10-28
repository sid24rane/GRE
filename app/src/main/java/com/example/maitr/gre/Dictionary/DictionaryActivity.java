package com.example.maitr.gre.Dictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.maitr.gre.R;
import com.yuyakaido.android.cardstackview.CardStackView;

public class DictionaryActivity extends AppCompatActivity {

    private CardStackView cardStackView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);


    }
}
