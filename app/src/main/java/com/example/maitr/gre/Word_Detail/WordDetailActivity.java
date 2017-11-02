package com.example.maitr.gre.Word_Detail;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class WordDetailActivity extends AppCompatActivity {

    private TextView word, meaning, synonym, antonym;
    private ImageView sound;
    private TextView sentence;
    private TextToSpeech textToSpeech;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);


        word = (TextView) findViewById(R.id.word);
        meaning = (TextView) findViewById(R.id.meaning);
        synonym = (TextView) findViewById(R.id.synonym);
        antonym = (TextView) findViewById(R.id.antonym);
        sound = (ImageView) findViewById(R.id.pronounce);
        sentence = (TextView) findViewById(R.id.sentence);


        textToSpeech = new TextToSpeech(WordDetailActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(WordDetailActivity.this, "This language is not supported!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(word.getText().toString());
                speakOut(meaning.getText().toString());
            }
        });

        load();
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

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(WordDetailActivity.this);
        progressDialog.setMessage("Loading word please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String word_id = getIntent().getStringExtra("word_id");

        if (word_id!=null){

            Log.d("word-id",word_id);

            DocumentReference ref = db.collection("words").document(word_id);

            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        DocumentSnapshot data = task.getResult();
                        if (data!=null){

                            Log.d("FIREBASE-WORD", "DocumentSnapshot data: " + task.getResult().getData());
                            word.setText(data.getString("word"));
                            meaning.setText(data.getString("meaning"));
                            sentence.setText(data.getString("sentence"));

                            // TODO: 1/11/17 fetch antonyms and synonyms

                            progressDialog.dismiss();
                        }else{
                            Log.d("FIREBASE-WORD", "NO data found");
                        }
                    }
                }
            });

        }
    }

    private void speakOut(String s) {
        textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
    }


}
