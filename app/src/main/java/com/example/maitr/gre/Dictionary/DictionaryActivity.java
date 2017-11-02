package com.example.maitr.gre.Dictionary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.maitr.gre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackView;

import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    private CardStackView cardStackView;
    private FlashCardAdapter flashCardAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        db = FirebaseFirestore.getInstance();

        cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);

        load();

    }

    private void load() {

        flashCardAdapter = new FlashCardAdapter(DictionaryActivity.this);
        flashCardAdapter.addAll(fetchFlashCards());
        cardStackView.setAdapter(flashCardAdapter);
    }

    private List<FlashCard> fetchFlashCards() {

        final List<FlashCard> data = new ArrayList<>();

        String level = getIntent().getStringExtra("level");

        db.collection("words")
                .whereEqualTo("level",level)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot doc:task.getResult()){
                                    String word  = doc.getString("word");
                                    String meaning = doc.getString("meaning");
                                    data.add(new FlashCard(word,meaning));
                                    flashCardAdapter.notifyDataSetChanged();
                                }
                                Log.d("FIREBASE-WL-FETCHED","success!");
                            }else{
                                Log.d("FIREBASE-WL-FETCHED","failed");
                            }
                    }
                });

        return data;
    }

}
