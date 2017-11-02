package com.example.maitr.gre.Dashboard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maitr.gre.Comprehension.ComprehensionsActivity;
import com.example.maitr.gre.Dictionary.DictionaryHomeActivity;
import com.example.maitr.gre.Jumbled_Words.JumbledWordsActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Word_Detail.TodayWord;
import com.example.maitr.gre.Word_Detail.WordDetailActivity;
import com.example.maitr.gre.Word_Meaning.MeaningActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class HomeFragment extends Fragment {

    private CardView wordOfTheDay;
    private CardView wordList;
    private CardView jumbledWords;
    private CardView wordMeaning;
    private CardView comprehension;
    private FirebaseFirestore db;
    private TextView wordofday;
    private TextView wordid;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        wordid = (TextView) view.findViewById(R.id.today_word_id);
        wordofday = (TextView) view.findViewById(R.id.today_word);
        wordOfTheDay = (CardView) view.findViewById(R.id.wordOfTheDay);
        wordList = (CardView) view.findViewById(R.id.wordList);
        jumbledWords = (CardView) view.findViewById(R.id.jumble);
        wordMeaning = (CardView) view.findViewById(R.id.wordMeaning);
        comprehension = (CardView) view.findViewById(R.id.comprehension);


        wordOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WordDetailActivity.class);
                i.putExtra("word_id",wordid.getText().toString());
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        wordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DictionaryHomeActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        jumbledWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), JumbledWordsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        wordMeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MeaningActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        comprehension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ComprehensionsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        fetchWordOfTheDay();

        return view;

    }


    private void fetchWordOfTheDay(){

        final String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String stored_date = StoredDate();

        if (stored_date == null || !(stored_date.equals(date))){

            final ArrayList<TodayWord> words = new ArrayList<>();

            db.collection("words")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()){

                                for (DocumentSnapshot doc : task.getResult()){
                                    words.add(new TodayWord(doc.getString("word"),doc.getId()));
                                }

                                Collections.shuffle(words);
                                String res = words.get(0).getWord();
                                String id = words.get(0).getId();
                                saveWordOfTheDay(res,date,id);
                                setWordOfTheDay(res,id);
                                Log.d("FIREBASE-WL-FETCHED","success!");
                            }else{
                                Log.d("FIREBASE-WOD-FETCHED","failed");
                            }
                        }
                    });
        }else{

            // same day
            // set text
            setWordOfTheDay();
        }

    }

    private void setWordOfTheDay(){
        wordofday.setText(getWordOfTheDay());
        wordid.setText(getWordId());
    }

    private void setWordOfTheDay(String word,String id){
        wordofday.setText(word);
        wordid.setText(id);
    }

    private String StoredDate() {

        SharedPreferences pref = getActivity().getSharedPreferences("word", 0); // 0 - for private mode
        return pref.getString("creation",null);
    }

    private String getWordId(){
        SharedPreferences pref = getActivity().getSharedPreferences("word", 0); // 0 - for private mode
        return pref.getString("word-id",null);
    }

    private String getWordOfTheDay(){
        SharedPreferences pref = getActivity().getSharedPreferences("word", 0); // 0 - for private mode
        return pref.getString("word-of-the-day",null);
    }

    private void saveWordOfTheDay(String res,String date,String id) {

        SharedPreferences pref = getActivity().getSharedPreferences("word", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("word-of-the-day",res);
        editor.putString("creation",date);
        editor.putString("word-id",id);
        editor.commit();

    }
}

