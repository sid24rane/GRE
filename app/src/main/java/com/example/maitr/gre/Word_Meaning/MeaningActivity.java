package com.example.maitr.gre.Word_Meaning;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MeaningActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView meaningWord, optionA, optionB, optionC, optionD, answer,meaningid;
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
        meaningid = (TextView) findViewById(R.id.meaningid);

        optionA = (TextView) findViewById(R.id.optionA);
        optionB = (TextView) findViewById(R.id.optionB);
        optionC = (TextView) findViewById(R.id.optionC);
        optionD = (TextView) findViewById(R.id.optionD);
        answer = (TextView) findViewById(R.id.meaningCorrectAnswer);
        next = (Button) findViewById(R.id.nextquestion);
        details = (Button) findViewById(R.id.details);

        answer.setVisibility(View.INVISIBLE);
        meaningid.setVisibility(View.INVISIBLE);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String current_word_id = meaningid.getText().toString();
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

                performCheck(optionA.getText().toString(),meaningWord.getText().toString(),optionA);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionB.getText().toString(),meaningWord.getText().toString(),optionB);

                optionA.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);

            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionC.getText().toString(),meaningWord.getText().toString(),optionC);

                optionB.setClickable(false);
                optionC.setClickable(false);
                optionA.setClickable(false);
            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionD.getText().toString(),meaningWord.getText().toString(),optionD);

                optionB.setClickable(false);
                optionC.setClickable(false);
                optionA.setClickable(false);

            }
        });

    }

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(MeaningActivity.this);
        progressDialog.setMessage("Loading words please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //// TODO: 1/11/17 only fetch nt solved user words
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
                                m.setId(document.getString("word_id"));
                                allwords.add(m);
                                answers_pairs.put(document.getString("word"),document.getString("Answer"));
                            }

                            // initial
                            display(nextQs());

                            progressDialog.dismiss();

                        } else {
                            Log.d("FIREBASE-Meaning", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void display(Meaning m){

        meaningid.setText(m.getId());
        meaningWord.setText(m.getWord());
        optionA.setText(m.getA());
        optionB.setText(m.getB());
        optionC.setText(m.getC());
        optionD.setText(m.getD());
        answer.setText(m.getAnswer());

    }

    private void performCheck(String selected,String current_word,TextView current_selected){

        if (selected.equals(answers_pairs.get(current_word))){

            // make green
            current_selected.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String meaning_id = meaningid.getText().toString();

            // update db
            markMeaning(userid,meaning_id);

            // show next qs
            display(nextQs());

        }else{

            // make red
            current_selected.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

            // enable show me answer button
            answer.setVisibility(View.VISIBLE);

        }

    }

    private void markMeaning(final String userid, final String meaning_id) {

        DocumentReference ref = db.collection("guess_meaning").document(meaning_id);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        DocumentSnapshot doc = task.getResult();

                        Map<String, Object> users = doc.getData();

                        if (users!=null){
                            int next = users.size();
                            users.put(String.valueOf(next),userid);
                        }else{
                            Map<String,Object> newuser = new HashMap<>();
                            newuser.put(String.valueOf(0),userid);
                        }


                        //update db
                        db.collection("guess_meaning").document(meaning_id)
                                .set(users)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.d("UPDATE-DB-MEANING","Success!");

                                        // update profile
                                        updateProfile(userid);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("UPDATE-DB-MEANING","failure!");
                                    }
                                });
                    }else{
                        Log.d("FIREBASE-INSERT-USER","failed");
                    }
            }
        });

    }

    private void updateProfile(final String userid){

        DocumentReference ref = db.collection("profiles").document(userid);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        // update
                        DocumentSnapshot doc = task.getResult();
                        int meaning = Integer.parseInt(doc.getString("guess_meaning"));
                        meaning++;

                        DocumentReference ref = db.collection("profiles").document(userid);
                        ref.update("guess_meaning",String.valueOf(meaning))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("FIREBASE-UPD-M-PROFILE","success!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FIREBASE-UPD-M-PROFILE","failed");
                                    }
                                });

                    }else{
                            Log.d("FIREBASE-UPD-M-PROFILE","failed to fetch user profile");
                    }
            }
        });

    }

    private Meaning nextQs(){
        return allwords.get(new Random().nextInt(allwords.size()));
    }
}
