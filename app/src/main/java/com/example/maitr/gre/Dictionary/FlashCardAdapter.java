package com.example.maitr.gre.Dictionary;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.example.maitr.gre.R;

import java.util.Locale;

/**
 * Created by sid24rane on 29/10/17.
 */

public class FlashCardAdapter extends ArrayAdapter<FlashCard>{

    private TextToSpeech textToSpeech;
    private SwipeDeck swipeDeck;

    FlashCardAdapter(Context context,SwipeDeck swipeDeck)
    {
        super(context,0);
        this.swipeDeck = swipeDeck;
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getContext(), "This language is not supported!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }

            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View contentView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (contentView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.item_flashcard, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);

        } else {

            holder = (ViewHolder) contentView.getTag();

        }

        final FlashCard flashCard = getItem(position);

        holder.term.setText(flashCard.getTerm());
        holder.definition.setText(flashCard.getDefinition());
        holder.sentence.setText(flashCard.getSentence());

        holder.speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    speakOut(flashCard.getTerm());
                    speakOut(flashCard.getDefinition());
            }
        });

        holder.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeDeck.swipeTopCardLeft(180);
            }
        });

        return contentView;
    }

    public void speakOut(String text){
        textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);
    }
    private static class ViewHolder {

        public TextView term;
        public TextView definition;
        public ImageView speak;
        public ImageView next;
        public TextView sentence;

        public ViewHolder(View view) {
            this.term = (TextView) view.findViewById(R.id.term);
            this.definition = (TextView) view.findViewById(R.id.definition);
            this.speak = (ImageView) view.findViewById(R.id.speak);
            this.sentence = (TextView) view.findViewById(R.id.sentence);
            this.next = (ImageView) view.findViewById(R.id.next);
        }
    }
}
