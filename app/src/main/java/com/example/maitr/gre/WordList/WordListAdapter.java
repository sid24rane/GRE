package com.example.maitr.gre.WordList;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maitr.gre.R;

import java.util.ArrayList;

/**
 * Created by sid24rane on 5/11/17.
 */

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> implements Filterable {

    private ArrayList<Word> allwords;

    public WordListAdapter(ArrayList<Word> allwords) {
        this.allwords = allwords;
    }

    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(WordListAdapter.ViewHolder holder, int position) {
        Word word = allwords.get(position);
        holder.word.setText(word.getWord());
        holder.word_id.setText(word.getWord_id());
        holder.meaning.setText(word.getMeaning());
        
    }

    @Override
    public int getItemCount() {
        return allwords.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<Word> filteredList = new ArrayList<>();

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    filteredList = allwords;
                } else {
                    filteredList = getFilteredResults(charString);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                allwords = (ArrayList<Word>) filterResults.values;
                Log.d("filtered-values",allwords.toString());
                WordListAdapter.this.notifyDataSetChanged();
            }
        };
    }

    protected ArrayList<Word> getFilteredResults(String query){

            ArrayList<Word> results = new ArrayList<>();
            for (Word word : allwords) {

                    String w = word.getWord().toString().toLowerCase();

                     if (w.startsWith(query)){
                            results.add(word);
                }
            }
            return results;
    }

    public void setData(ArrayList<Word> data) {
        this.allwords = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView word;
        public TextView word_id;
        public TextView meaning;
        public ImageView moreinfo;

        public ViewHolder(View view) {
            super(view);

            this.moreinfo = (ImageView) view.findViewById(R.id.moreinfo);
            this.word = (TextView) view.findViewById(R.id.word);
            this.word_id = (TextView) view.findViewById(R.id.wordid);
            this.meaning = (TextView) view.findViewById(R.id.meaning);
        }
    }
}
