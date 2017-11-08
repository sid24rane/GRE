package com.example.maitr.gre.WordList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.maitr.gre.Dashboard.DashboardActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Word_Detail.WordDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WordListFragment extends Fragment{

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ArrayList<Word> allwords = new ArrayList<>();
    private WordListAdapter wordListAdapter;

    public WordListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_word_list, container, false);


        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();

        recyclerView = (RecyclerView) view.findViewById(R.id.wordlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                       Word word = allwords.get(position);
                        Intent i = new Intent(getContext(), WordDetailActivity.class);
                        i.putExtra("word_id",word.getWord_id());
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                    }
                })
        );

        wordListAdapter = new WordListAdapter(allwords);
        recyclerView.setAdapter(wordListAdapter);

        load();

        return view;
    }

    private void load() {


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
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
                                        Word word = new Word();
                                        word.setWord_id(doc.getId());
                                        word.setWord(doc.getString("word"));
                                        word.setMeaning(doc.getString("meaning"));
                                        allwords.add(word);
                                    }

                                    Collections.sort(allwords, new Comparator<Word>() {
                                        @Override
                                        public int compare(Word word, Word t1) {
                                           return word.getWord().compareTo(t1.getWord());
                                        }
                                    });

                                    progressDialog.dismiss();
                                    wordListAdapter.notifyDataSetChanged();
                                }else{
                                    Log.d("FIREBASE-WL","failed!");
                                }
                    }
                });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((DashboardActivity) getActivity()).getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search words");
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                wordListAdapter.setData(allwords);
                wordListAdapter.notifyDataSetChanged();
                wordListAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

}
