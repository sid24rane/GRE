package com.example.maitr.gre.Dashboard;

import android.app.SearchManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.maitr.gre.R;
import com.example.maitr.gre.WordList.WordListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FirebaseFirestore db;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container,new HomeFragment()).commit();
                        return true;
                    case R.id.navigation_profile:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container,new ProfileFragment()).commit();
                        return true;
                    case R.id.navigation_wordlist:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container,new WordListFragment()).commit();
                        return true;
                }
                return false;
            }
        });

        fragmentTransaction.replace(R.id.container, new HomeFragment()).commit();

        loadTotal();

    }


    private void loadTotal() {

        db.collection("comprehension").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                             saveData(String.valueOf(task.getResult().size()),"comprehension");
                        }
                    }
                });

        db.collection("guess_meaning").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            saveData(String.valueOf(task.getResult().size()),"guess_meaning");
                        }
                    }
                });

        db.collection("jumbled_words").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            saveData(String.valueOf(task.getResult().size()),"jumbled_words");
                        }
                    }
                });

        db.collection("words").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            saveData(String.valueOf(task.getResult().size()),"words");
                            saveData(String.valueOf(task.getResult().size()),"echo");
                        }
                    }
                });

    }

    private void saveData(String size, String type){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("total", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(type,size);
        editor.commit();

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Press BACK button again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
