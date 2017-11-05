package com.example.maitr.gre.Jumbled_Words;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.DoneActivity;
import com.example.maitr.gre.Echo.EchoActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Word_Meaning.Meaning;
import com.example.maitr.gre.Word_Meaning.MeaningActivity;
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
import java.util.Iterator;
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
    private String userid;
    private Jumbled current;
    private TextView correctanswer;
    private boolean isAnsAlreadyShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        db = FirebaseFirestore.getInstance();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        correctanswer = (TextView) findViewById(R.id.CorrectAnswer);


        next_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAnsAlreadyShown){

                    // next qs
                    display(nextQs());

                }else{

                    String userin = answer.getText().toString().toLowerCase().trim();

                    if (userin.length() > 0){

                        // perform check
                        performCheck(userin);

                    }else{

                        // next qs
                        display(nextQs());
                    }
                }

            }
        });


        showanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    isAnsAlreadyShown = true;
                    correctanswer.setVisibility(View.VISIBLE);
            }
        });

    }

    private void performCheck(String user_ans){

        String correct_answer = correctanswer.getText().toString().toLowerCase().split(" ")[1].trim();

        if (user_ans.equals(correct_answer)){

            // remove current qs from list
            Iterator<Jumbled> iter = allwords.iterator();
            while (iter.hasNext()) {
                Jumbled m = iter.next();
                String s = m.getWord();
                String st = jumbledWord.getText().toString();
                if (s.equals(st)){
                    iter.remove();
                }
            }

            // update db
            markJumbled();

            //show next qs
            display(nextQs());
        }else{
            wrongAns();
        }
    }

    private void wrongAns() {

        answer.setText("");
        Toast.makeText(JumbledWordsActivity.this, "Oops, Wrong answer! Try again", Toast.LENGTH_SHORT).show();
    }

    private void markJumbled(){

        HashMap<String,Object> data = new HashMap<>();

        ArrayList<String> users = current.getUsers();

        if (users.size() == 1 && (users.get(0)).equals("0")){
            users.remove(0);
            users.add(userid);
        }else{
            users.add(userid);
        }

        data.put("users",users);


        //update db
        DocumentReference ref = db.collection("jumbled_words").document(current.getId());
        ref.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("UPDATE-DB-JUMBLED","Success!");

                        // update profile
                        updateProfile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPDATE-DB-JUMBLED","failure!");
                        Log.d("reason",e.getMessage());
                    }
                });
    }

    private void display(Jumbled jumbled){

        current = jumbled;
        isAnsAlreadyShown = false;

        answer.setText("");
        hint.setText("Hint : " + jumbled.getHint());
        jumbledWord.setText(jumbled.getWord());

        correctanswer.setText("Answer: " + jumbled.getAnswer());
        jumbledwordid.setText(jumbled.getId());

        correctanswer.setVisibility(View.INVISIBLE);
        jumbledwordid.setVisibility(View.GONE);

    }


    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(JumbledWordsActivity.this);
        progressDialog.setMessage("Loading words please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("jumbled_words")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document:task.getResult()){

                                Log.d("FIREBASE-JUMBLED", document.getId() + " => " + document.getData());

                                ArrayList<String> users = (ArrayList<String>)document.get("users");

                                if (!(alreadySolved(users))){

                                    Jumbled j = new Jumbled();
                                    j.setId(document.getId());
                                    j.setAnswer(document.getString("answer"));
                                    j.setHint(document.getString("hint"));
                                    j.setWord(document.getString("word"));
                                    j.setUsers((ArrayList<String>) document.get("users"));
                                    allwords.add(j);
                                }

                            }
                            progressDialog.dismiss();

                            if (!(allwords.isEmpty())) {

                                Log.d("allqs-size",String.valueOf(allwords.size()));

                                // initial
                                display(nextQs());

                            }else{
                                done();
                            }
                        }else{
                            Log.d("FIREBASE-JUM-FAILED","FAILED!");
                        }
                    }
                });
    }

    private boolean alreadySolved(ArrayList<String> users){

        Iterator<String> iter = users.iterator();
        while (iter.hasNext()) {
            String muser = iter.next();
            if (muser.equals(userid)){
                return true;
            }
        }
        return false;
    }

    private void done() {
        Intent i = new Intent(JumbledWordsActivity.this,DoneActivity.class);
        i.putExtra("from","jumbled-words");
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private void updateProfile(){

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
        if (allwords.isEmpty()){
            done();
        }else{
            return allwords.get(new Random().nextInt(allwords.size()));
        }
        return null;
    }

}
