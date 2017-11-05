package com.example.maitr.gre.Echo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.DoneActivity;
import com.example.maitr.gre.Jumbled_Words.Jumbled;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class EchoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Echo> allwords = new ArrayList<>();
    private ImageView wordspeak;
    private Button next_skip;
    private EditText user_word;
    private TextToSpeech textToSpeech;
    private TextView word;
    private String userid;
    private Echo current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);

        db = FirebaseFirestore.getInstance();
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        textToSpeech = new TextToSpeech(EchoActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(EchoActivity.this, "This language is not supported!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        init();
        load();

    }

    private void init() {

        wordspeak = (ImageView) findViewById(R.id.wordspeak);
        next_skip = (Button) findViewById(R.id.next_skip);
        user_word = (EditText) findViewById(R.id.user_word);
        word = (TextView) findViewById(R.id.current_word);

        wordspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 speakOut(word.getText().toString());
            }
        });

        next_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_ans = user_word.getText().toString().toLowerCase().trim();

                Log.d("user_ans =>",user_ans);
                Log.d("word => ", word.getText().toString());

                if (user_ans.length() > 0){

                     if (user_ans.equals(word.getText().toString())){

                         performDbUpdate();

                     }else{
                         wrongAns();
                     }

                }else{
                    display(nextWord());
                }
            }
        });

    }

    private void performDbUpdate(){

        // remove current qs from list
        Iterator<Echo> iter = allwords.iterator();
        while (iter.hasNext()) {
            Echo m = iter.next();
            String s = m.getWord();
            String st = word.getText().toString();
            if (s.equals(st)){
                iter.remove();
            }
        }

        markEcho();
        display(nextWord());
    }

    private void markEcho() {

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
        DocumentReference ref = db.collection("words").document(current.getWord_id());
        ref.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("UPDATE-DB-ECHO","Success!");

                        // update profile
                        updateProfile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPDATE-DB-ECHO","failure!");
                        Log.d("reason",e.getMessage());
                    }
                });
    }

    private void updateProfile() {

        DocumentReference ref = db.collection("profiles").document(userid);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    // update
                    DocumentSnapshot doc = task.getResult();
                    int echos = Integer.parseInt(doc.getString("echo"));
                    echos++;

                    DocumentReference ref = db.collection("profiles").document(userid);
                    ref.update("echo",String.valueOf(echos))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE-UPD-E-PROFILE","success!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FIREBASE-UPD-E-PROFILE","failed");
                                }
                            });

                }else{
                    Log.d("FIREBASE-UPD-E-PROFILE","failed to fetch user profile");
                }
            }
        });
    }

    private void wrongAns() {

        user_word.setText("");
        Toast.makeText(EchoActivity.this, "Oops, Wrong answer! Try again", Toast.LENGTH_SHORT).show();

    }

    private void done() {

        Intent i = new Intent(EchoActivity.this,DoneActivity.class);
        i.putExtra("from","echo");
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    private Echo nextWord() {

        if (allwords.isEmpty()){
            done();
        }else{
            return allwords.get(new Random().nextInt(allwords.size()));
        }
        return null;
    }

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(EchoActivity.this);
        progressDialog.setMessage("Loading words please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("words")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){

                                for (DocumentSnapshot doc:task.getResult()){

                                    Log.d("FIREBASE-ECHO", doc.getId() + " => " + doc.getData());

                                    ArrayList<String> users = (ArrayList<String>)doc.get("users");

                                    if (!(alreadySolved(users))){

                                        Echo e = new Echo();
                                        e.setWord(doc.getString("word"));
                                        e.setWord_id(doc.getId());
                                        e.setUsers((ArrayList<String>) doc.get("users"));
                                        allwords.add(e);

                                    }

                                }

                                progressDialog.dismiss();

                                if (!(allwords.isEmpty())) {

                                    Log.d("allqs-size",String.valueOf(allwords.size()));

                                    // initial
                                    display(nextWord());
                                }else{
                                    done();
                                }
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


    private void display(Echo new_word){

        current = new_word;

        user_word.setText("");
        word.setText(new_word.getWord().toLowerCase().trim());
    }

    private void speakOut(String s) {
        textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
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

}
