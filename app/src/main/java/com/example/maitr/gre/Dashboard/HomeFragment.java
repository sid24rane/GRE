package com.example.maitr.gre.Dashboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maitr.gre.Dictionary.DictionaryActivity;
import com.example.maitr.gre.Dictionary.DictionaryHomeActivity;
import com.example.maitr.gre.Jumbled_Words.JumbledWordsActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.WordDetailActivity;
import com.example.maitr.gre.Word_Meaning.MeaningActivity;


public class HomeFragment extends Fragment {

    private CardView wordOfTheDay;
    private CardView wordList;
    private CardView jumbledWords;
    private CardView wordMeaning;
    private CardView comprehension;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        wordOfTheDay = (CardView) view.findViewById(R.id.wordOfTheDay);
        wordList = (CardView) view.findViewById(R.id.wordList);
        jumbledWords = (CardView) view.findViewById(R.id.jumble);
        wordMeaning = (CardView) view.findViewById(R.id.wordMeaning);
        comprehension = (CardView) view.findViewById(R.id.comprehension);


        wordOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), WordDetailActivity.class);
                startActivity(i);
            }
        });

        wordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DictionaryHomeActivity.class);
                startActivity(i);
            }
        });

        jumbledWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), JumbledWordsActivity.class);
                startActivity(i);
            }
        });

        wordMeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MeaningActivity.class);
                startActivity(i);
            }
        });

        comprehension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MeaningActivity.class);
                startActivity(i);
            }
        });

        return view;

    }

}
