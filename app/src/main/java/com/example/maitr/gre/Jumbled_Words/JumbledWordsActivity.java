package com.example.maitr.gre.Jumbled_Words;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maitr.gre.R;
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

public class JumbledWordsActivity extends AppCompatActivity {

    private TextView jumbledWord;
    private TextView hint;
    private EditText answer;
    private TextView jumbledwordid;
    private ArrayList<Jumbled> allwords = new ArrayList<>();
    private FirebaseFirestore db;
    private Button next_check;
    private Button showanswer;
    private HashMap<String,Object> answer_pairs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        db = FirebaseFirestore.getInstance();

        init();
        load();

    }

    private void init() {

        jumbledWord = (TextView) findViewById(R.id.jumbledWord);
        hint = (TextView) findViewById(R.id.hintMeaning);
        answer = (EditText) findViewById(R.id.jumbledAnswer);
        jumbledwordid = (TextView) findViewById(R.id.jumbledWordid);
        showanswer = (Button) findViewById(R.id.wordanswer);
        next_check = (Button) findViewById(R.id.nextword);

        jumbledwordid.setVisibility(View.INVISIBLE);
        showanswer.setVisibility(View.INVISIBLE);

        next_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userin = answer.getText().toString();

                if (userin.length() > 0){

                    // perform check
                    performCheck(jumbledWord.getText().toString(),userin);

                }else{
                    // next qs
                    display(nextQs());
                }
            }
        });


        showanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    answer.setText(getAnswer(jumbledWord.getText().toString()));
            }
        });

    }

    private String getAnswer(String word){
            return (String) answer_pairs.get(word);
    }

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(JumbledWordsActivity.this);
        progressDialog.setMessage("Loading profile please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //// TODO: 1/11/17 only fetch those qs nt solved yet
        db.collection("jumbled_words")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot document:task.getResult()){
                                    Log.d("FIREBASE-JUMBLED", document.getId() + " => " + document.getData());
                                    Jumbled j = new Jumbled();
                                    j.setId(document.getId());
                                    j.setAnswer(document.getString("answer"));
                                    j.setHint(document.getString("hint"));
                                    j.setWord(document.getString("word"));
                                    allwords.add(j);
                                    answer_pairs.put(document.getString("word"),document.getString("answer"));
                                }

                                // initial
                                display(nextQs());

                                progressDialog.dismiss();

                            }else{
                                Log.d("FIREBASE-JUM-FAILED","FAILED!");
                            }
                    }
                });
    }

    private void performCheck(String word, String user_answer){

            String ans = getAnswer(word);

            if (ans.equals(user_answer)){

                // update db
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                markJumbled(userid,jumbledwordid.getText().toString());

                // display next qs
                display(nextQs());

            }else{
                // wrong answer
                showanswer.setVisibility(View.VISIBLE);
                //change bg of ans
                answer.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            }
    }

    private void display(Jumbled jumbled){

        hint.setText(jumbled.getHint());
        answer.setText(jumbled.getWord());
        jumbledWord.setText(jumbled.getWord());
        jumbledwordid.setText(jumbled.getId());

    }


    private void markJumbled(final String userid, final String jumbled_id) {

        DocumentReference ref = db.collection("jumbled_words").document(jumbled_id);

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
                    db.collection("jumbled_words").document(jumbled_id)
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
                    int jumbled = Integer.parseInt(doc.getString("jumbled_words"));
                    jumbled++;

                    DocumentReference ref = db.collection("profiles").document(userid);
                    ref.update("jumbled_words",String.valueOf(jumbled))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE-UPD-J-PROFILE","success!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FIREBASE-UPD-J-PROFILE","failed");
                                }
                            });

                }else{
                    Log.d("FIREBASE-UPD-M-PROFILE","failed to fetch user profile");
                }
            }
        });

    }

    private Jumbled nextQs(){
        return allwords.get(new Random().nextInt(allwords.size()));
    }

}
