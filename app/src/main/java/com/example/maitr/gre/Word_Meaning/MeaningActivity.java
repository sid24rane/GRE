package com.example.maitr.gre.Word_Meaning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.DoneActivity;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MeaningActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView meaningWord, optionA, optionB, optionC, optionD, answer,meaningid;
    private ArrayList<Meaning> allwords = new ArrayList<>();
    private Button next;
    private Button details;
    private ImageView speak;
    private TextToSpeech textToSpeech;
    private String userid;
    private Meaning current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        db = FirebaseFirestore.getInstance();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();
        load();

    }


    private void init() {


        textToSpeech = new TextToSpeech(MeaningActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MeaningActivity.this, "This language is not supported!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        meaningWord = (TextView) findViewById(R.id.meaningWord);
        meaningid = (TextView) findViewById(R.id.meaningid);
        speak = (ImageView) findViewById(R.id.speak);

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

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                           speakOut(meaningWord.getText().toString());
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
                performCheck(optionA);
            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCheck(optionB);
            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCheck(optionC);
            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performCheck(optionD);
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

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(MeaningActivity.this);
        progressDialog.setMessage("Loading words please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("guess_meaning")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("FIREBASE-Meaning", document.getId() + " => " + document.getData());

                                ArrayList<String> users = (ArrayList<String>)document.get("users");

                                if (!(alreadySolved(users))){

                                    Meaning m = new Meaning();
                                    m.setMeaningid(document.getId());
                                    m.setA(document.getString("A"));
                                    m.setB(document.getString("B"));
                                    m.setC(document.getString("C"));
                                    m.setD(document.getString("D"));
                                    m.setWord(document.getString("word"));
                                    m.setAnswer(document.getString("Answer"));
                                    m.setId(document.getString("word_id"));
                                    m.setUsers((ArrayList<String>) document.get("users"));
                                    allwords.add(m);

                                }

                            }

                            progressDialog.dismiss();

                            if (!(allwords.isEmpty())) {

                                Log.d("allqs",allwords.toString());
                                Log.d("allqs-size",String.valueOf(allwords.size()));

                                // initial
                                display(nextQs());
                            }else{
                                done();
                            }

                        } else {
                            Log.d("FIREBASE-Meaning", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void done() {
        Intent i = new Intent(MeaningActivity.this,DoneActivity.class);
        i.putExtra("from","word-meaning");
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    private void display(Meaning m){

        resetViewBg();

        current = m;

        meaningWord.setText(m.getWord());
        optionA.setText("A) " + m.getA());
        optionB.setText("B) " +m.getB());
        optionC.setText("C) "+m.getC());
        optionD.setText("D) "+m.getD());

        answer.setVisibility(View.INVISIBLE);
        meaningid.setText(m.getId());
        answer.setText(m.getAnswer());

    }

    private void resetViewBg(){

        optionA.setTextColor(Color.parseColor("#808080"));
        optionB.setTextColor(Color.parseColor("#808080"));
        optionC.setTextColor(Color.parseColor("#808080"));
        optionD.setTextColor(Color.parseColor("#808080"));
    }

    private void performCheck(final TextView current_selected) {


        String selected = (current_selected.getText().toString()).split(" ")[1];

        if (selected.equals(answer.getText().toString())){

            // make green
            current_selected.setTextColor(Color.parseColor("#00d86f"));


            // remove current qs from list
            Iterator<Meaning> iter = allwords.iterator();

            while (iter.hasNext()) {
                Meaning m = iter.next();
                String s = m.getWord();
                String st = meaningWord.getText().toString();
                if (s.equals(st)){
                    iter.remove();
                }
            }

            // update db
            markMeaning();

            // show next qs
            display(nextQs());

        }else{

            // make red
            current_selected.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        }

    }

    private void markMeaning() {

        HashMap<String,Object> data = new HashMap<>();
        ArrayList<String> users = current.getUsers();
        if (users.size() == 1 && (users.get(0)).equals("0")){
            users.remove(0);
            users.add(userid);
        }else{
            users.add(userid);
        }
        data.put("users",users);

        Log.d("current=>",users.toString());

        //update db
        DocumentReference ref = db.collection("guess_meaning").document(current.getMeaningid());
        ref.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("UPDATE-DB-MEANING","Success!");

                        // update profile
                        updateProfile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPDATE-DB-MEANING","failure!");
                        Log.d("reason",e.getMessage());
                    }
                });
    }

    private void speakOut(String s) {
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
    }

    private void updateProfile(){

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

    @Override
    protected void onDestroy() {

        //Close the Text to Speech Library
        if(textToSpeech != null) {

            textToSpeech.stop();
            textToSpeech.shutdown();
            Log.d("TEXT TO SPEECH", "TTS Destroyed");
        }
        super.onDestroy();

    }

    private Meaning nextQs(){
        if (allwords.isEmpty()){
            done();
        }else{
            return allwords.get(new Random().nextInt(allwords.size()));
        }
        return null;
    }
}
