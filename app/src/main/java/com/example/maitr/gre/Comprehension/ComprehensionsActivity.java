package com.example.maitr.gre.Comprehension;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.maitr.gre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComprehensionsActivity extends AppCompatActivity {

    private TextView qs, optionA, optionB, optionC, optionD, answer,qsid;
    private Button next;
    private FirebaseFirestore db;
    private ArrayList<Comprehension> allqs = new ArrayList<>();
    private HashMap<String,String> answers_pairs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensions);


        db = FirebaseFirestore.getInstance();

        init();
        load();

    }

    private void load() {


        db.collection("comprehensions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FIREBASE-COMPREHENSIONS", document.getId() + " => " + document.getData());
                                Comprehension m = new Comprehension();
                                m.setA(document.getString("A"));
                                m.setB(document.getString("B"));
                                m.setC(document.getString("C"));
                                m.setD(document.getString("D"));
                                m.setQuestion(document.getString("question"));
                                m.setAnswer(document.getString("Answer"));
                                m.setId(document.getId());
                                allqs.add(m);
                                answers_pairs.put(document.getString("question"),document.getString("Answer"));

                                // initial

                                display(nextQs());

                            }
                        } else {
                            Log.d("FIREBASE-Meaning", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void init() {

        qs = (TextView) findViewById(R.id.comprehensionQuestion);
        qsid = (TextView) findViewById(R.id.questionid);

        optionA = (TextView) findViewById(R.id.compOptionA);
        optionB = (TextView) findViewById(R.id.compOptionB);
        optionC = (TextView) findViewById(R.id.compOptionC);
        optionD = (TextView) findViewById(R.id.compOptionD);
        answer = (TextView) findViewById(R.id.comprehensionCorrectAnswer);
        next = (Button) findViewById(R.id.nextquestion);

        answer.setVisibility(View.INVISIBLE);
        qsid.setVisibility(View.INVISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display(nextQs());
            }
        });

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionA.getText().toString(),qs.getText().toString(),optionA);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionB.getText().toString(),qs.getText().toString(),optionB);
                optionA.setClickable(false);
                optionC.setClickable(false);
                optionD.setClickable(false);
            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionC.getText().toString(),qs.getText().toString(),optionC);
                optionB.setClickable(false);
                optionA.setClickable(false);
                optionD.setClickable(false);
            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performCheck(optionD.getText().toString(),qs.getText().toString(),optionD);
                optionB.setClickable(false);
                optionC.setClickable(false);
                optionA.setClickable(false);

            }
        });
    }

    private void display(Comprehension c){

        qs.setText(c.getQuestion());
        optionA.setText(c.getA());
        optionB.setText(c.getB());
        optionC.setText(c.getC());
        optionD.setText(c.getD());
        answer.setText(c.getAnswer());
    }

    private void performCheck(String selected,String current_word,TextView current_selected){

        if (selected.equals(answers_pairs.get(current_word))){

            // make green
            current_selected.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

            String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // update db
            markComprehensions(userid,qsid.getText().toString());

        }else{

            // mark red
            current_selected.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

            // show answer
            answer.setVisibility(View.VISIBLE);
        }

        display(nextQs());
    }

    private void markComprehensions(final String userid, final String comprehension_id) {

        DocumentReference ref = db.collection("comprehension").document(comprehension_id);

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
                    db.collection("comprehension").document(comprehension_id)
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


    private Comprehension nextQs(){
        return allqs.get(new Random().nextInt(allqs.size()));
    }

    private void updateProfile(final String userid){

        DocumentReference ref = db.collection("profiles").document(userid);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    // update
                    DocumentSnapshot doc = task.getResult();
                    int comprehensions = Integer.parseInt(doc.getString("comprehensions"));
                    comprehensions++;

                    DocumentReference ref = db.collection("profiles").document(userid);
                    ref.update("comprehensions",String.valueOf(comprehensions))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE-UPD-C-PROFILE","success!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FIREBASE-UPD-C-PROFILE","failed");
                                }
                            });

                }else{
                    Log.d("FIREBASE-UPD-C-PROFILE","failed to fetch user profile");
                }
            }
        });

    }


}
