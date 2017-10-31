package com.example.maitr.gre.Word_Meaning;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.maitr.gre.R;
import com.example.maitr.gre.Word_Detail.WordDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MeaningActivity extends AppCompatActivity {

    private int user_meaning_score;
    private FirebaseFirestore db;
    private TextView meaningWord, optionA, optionB, optionC, optionD, answer;
    private ArrayList<Meaning> allwords = new ArrayList<>();
    private HashMap<String,String> answers_pairs = new HashMap<>();
    private Button next;
    private Button details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        db = FirebaseFirestore.getInstance();

        init();
        load();
    }

    private void init() {

        meaningWord = (TextView) findViewById(R.id.meaningWord);
        optionA = (TextView) findViewById(R.id.optionA);
        optionB = (TextView) findViewById(R.id.optionB);
        optionC = (TextView) findViewById(R.id.optionC);
        optionD = (TextView) findViewById(R.id.optionD);
        answer = (TextView) findViewById(R.id.meaningCorrectAnswer);
        next = (Button) findViewById(R.id.nextquestion);
        details = (Button) findViewById(R.id.details);

        answer.setVisibility(View.INVISIBLE);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_word_id = "";
                Intent i = new Intent(MeaningActivity.this, WordDetailActivity.class);
                i.putExtra("word_id",current_word_id);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display(nextQs());
            }
        });

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionA.getText().toString(),meaningWord.getText().toString());
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionB.getText().toString(),meaningWord.getText().toString());

                optionA.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);

            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionC.getText().toString(),meaningWord.getText().toString());

                optionB.setClickable(false);
                optionC.setClickable(false);
                optionA.setClickable(false);
            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionD.getText().toString(),meaningWord.getText().toString());

                optionB.setClickable(false);
                optionC.setClickable(false);
                optionA.setClickable(false);

            }
        });

    }

    private void load() {

        db.collection("guess_meaning")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FIREBASE-Meaning", document.getId() + " => " + document.getData());
                                Meaning m = new Meaning();
                                m.setA(document.getString("A"));
                                m.setB(document.getString("B"));
                                m.setC(document.getString("C"));
                                m.setD(document.getString("D"));
                                m.setWord(document.getString("word"));
                                m.setAnswer(document.getString("Answer"));
                                allwords.add(m);
                                answers_pairs.put(document.getString("word"),document.getString("Answer"));
                            }
                        } else {
                            Log.d("FIREBASE-Meaning", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void display(Meaning m){

        meaningWord.setText(m.getWord());
        optionA.setText(m.getA());
        optionB.setText(m.getB());
        optionC.setText(m.getC());
        optionD.setText(m.getD());
        answer.setText(m.getAnswer());

    }

    private void performCheck(String selected,String current_word){

        if (selected.equals(answers_pairs.get(current_word))){
            // mark & store
        }else{
            // show answer
            // mark
            answer.setVisibility(View.VISIBLE);
        }

        display(nextQs());
    }

    private Meaning nextQs(){
        return allwords.get(new Random().nextInt(allwords.size()));
    }
}
