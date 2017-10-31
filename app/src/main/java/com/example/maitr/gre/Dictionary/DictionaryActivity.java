package com.example.maitr.gre.Dictionary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.maitr.gre.R;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    private CardStackView cardStackView;
    private FlashCardAdapter flashCardAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
        progressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);

        load();

    }

    private void load() {

        flashCardAdapter = new FlashCardAdapter(DictionaryActivity.this);
        flashCardAdapter.addAll(fetchFlashCards());
        cardStackView.setAdapter(flashCardAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private List<FlashCard> fetchFlashCards() {
        List<FlashCard> data = new ArrayList<>();
        data.add(new FlashCard("Lol","Laughing out loud"));
        data.add(new FlashCard("LMAO","Laughing my ass off"));
        data.add(new FlashCard("HAHA","Laughing"));
        data.add(new FlashCard("Lol","Laughing out loud"));
        data.add(new FlashCard("LMAO","Laughing my ass off"));
        data.add(new FlashCard("HAHA","Laughing"));
        data.add(new FlashCard("Lol","Laughing out loud"));
        data.add(new FlashCard("LMAO","Laughing my ass off"));
        data.add(new FlashCard("HAHA","Laughing"));
        return data;
    }

}
