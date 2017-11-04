package com.example.maitr.gre;

import android.app.ProgressDialog;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.Word_Detail.WordDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class EchoActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<String> allwords = new ArrayList<>();
    private ImageView wordspeak;
    private ImageView next_skip;
    private EditText user_word;
    private TextToSpeech textToSpeech;
    private TextView word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);

        db = FirebaseFirestore.getInstance();

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
        next_skip = (ImageView) findViewById(R.id.next_skip);
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

                String user_ans = user_word.getText().toString();

                if (user_ans.length() > 0){

                     if (user_ans.equals(word.getText().toString())){
                         display(nextWord());
                     }else{
                         wrongAns();
                     }

                }else{
                    display(nextWord());
                }
            }
        });

    }

    private void wrongAns() {
        Toast.makeText(EchoActivity.this, "Oops, Wrong answer! Try again", Toast.LENGTH_SHORT).show();
    }

    private String nextWord() {
        return allwords.get(new Random().nextInt(allwords.size()));
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
                                    String word = doc.getString("word");
                                    allwords.add(word);
                                }
                                progressDialog.dismiss();
                                display(nextWord());
                            }
                    }
                });
    }

    private void display(String new_word){
        word.setText(new_word);
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
