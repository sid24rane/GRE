package com.example.maitr.gre.Dictionary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.example.maitr.gre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DictionaryActivity extends AppCompatActivity {

    private SwipeDeck swipeDeck;
    private FlashCardAdapter flashCardAdapter;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        db = FirebaseFirestore.getInstance();

        swipeDeck = (SwipeDeck) findViewById(R.id.swipe_deck);
        load();

    }

    private void load() {

        progressDialog = new ProgressDialog(DictionaryActivity.this);
        progressDialog.setMessage("Loading dictionary please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        flashCardAdapter = new FlashCardAdapter(DictionaryActivity.this,swipeDeck);
        swipeDeck.setAdapter(flashCardAdapter);
        fetchFlashCards();
    }


    private void fetchFlashCards() {


        final int level = Integer.parseInt(getIntent().getStringExtra("level"));

        db.collection("words")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot doc:task.getResult()){
                                    int word_level  = (int) (long) doc.get("level");
                                    if (word_level == level){
                                        Log.d("FIREBASE-Meaning", doc.getId() + " => " + doc.getString("word"));
                                        String word  = doc.getString("word");
                                        String meaning = doc.getString("meaning");
                                        String sentence = doc.getString("sentence");
                                        flashCardAdapter.add(new FlashCard(word,meaning,sentence));
                                        flashCardAdapter.notifyDataSetChanged();
                                    }
                                }
                                Log.d("FIREBASE-WL-FETCHED","success!");
                                progressDialog.dismiss();
                            }else{
                                Log.d("FIREBASE-WL-FETCHED","failed");
                            }
                    }
                });

    }

}
