package com.example.maitr.gre.Comprehension;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.maitr.gre.DoneActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Word_Meaning.Meaning;
import com.example.maitr.gre.Word_Meaning.MeaningActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ComprehensionsActivity extends AppCompatActivity {

    private TextView qs, optionA, optionB, optionC, optionD, answer,qsid;
    private Button next;
    private FirebaseFirestore db;
    private ArrayList<Comprehension> allqs = new ArrayList<>();
    private String userid;
    private Comprehension current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensions);

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        init();
        load();

    }

    private void init() {

        qs = (TextView) findViewById(R.id.comprehensionQuestion);
        qsid = (TextView) findViewById(R.id.questionid);

        optionA = (TextView) findViewById(R.id.compOptionA);
        optionB = (TextView) findViewById(R.id.compOptionB);
        optionC = (TextView) findViewById(R.id.compOptionC);
        optionD = (TextView) findViewById(R.id.compOptionD);
        answer = (TextView) findViewById(R.id.comprehensionCorrectAnswer);
        next = (Button) findViewById(R.id.nextquestion);

        answer.setVisibility(View.INVISIBLE);
        qsid.setVisibility(View.INVISIBLE);

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

        final ProgressDialog progressDialog = new ProgressDialog(ComprehensionsActivity.this);
        progressDialog.setMessage("Loading questions please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection("comprehension")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot document : task.getResult()) {

                                Log.d("FIREBASE-COMPREHENSIONS", document.getId() + " => " + document.getData());

                                ArrayList<String> users = (ArrayList<String>)document.get("users");

                                if (!(alreadySolved(users))){

                                    Comprehension m = new Comprehension();
                                    m.setA(document.getString("A"));
                                    m.setB(document.getString("B"));
                                    m.setC(document.getString("C"));
                                    m.setD(document.getString("D"));
                                    m.setQuestion(document.getString("question"));
                                    m.setAnswer(document.getString("Answer"));
                                    m.setId(document.getId());
                                    m.setUsers((ArrayList<String>) document.get("users"));
                                    allqs.add(m);

                                }
                            }
                            progressDialog.dismiss();

                            if (!(allqs.isEmpty())) {

                                Log.d("allqs-size",String.valueOf(allqs.size()));

                                // initial
                                display(nextQs());
                            }else{
                                done();
                            }

                        } else {
                            Log.d("FIREBASE-Comprehensions", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void done() {

        Intent i = new Intent(ComprehensionsActivity.this,DoneActivity.class);
        i.putExtra("from","comprehensions");
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    private void display(Comprehension c){

        resetViewBg();

        current = c;

        qs.setText(c.getQuestion());
        optionA.setText("A) "+c.getA());
        optionB.setText("B) "+c.getB());
        optionC.setText("C) " + c.getC());
        optionD.setText("D) " + c.getD());

        answer.setText(c.getAnswer());
        answer.setVisibility(View.INVISIBLE);
        qsid.setText(c.getId());
        qsid.setVisibility(View.INVISIBLE);
    }


    private void resetViewBg(){

        optionA.setBackgroundColor(Color.parseColor("#D3D3D3"));
        optionB.setBackgroundColor(Color.parseColor("#D3D3D3"));
        optionC.setBackgroundColor(Color.parseColor("#D3D3D3"));
        optionD.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    private void performCheck(final TextView current_selected) {


        String selected = (current_selected.getText().toString()).split(" ")[1];

        Log.d("selected =>",selected);
        Log.d("answer =>",answer.getText().toString());

        if (selected.contentEquals(answer.getText().toString())){

            // make green
            current_selected.setBackgroundColor(Color.parseColor("#00d86f"));

            // remove current qs from list
            Iterator<Comprehension> iter = allqs.iterator();

            while (iter.hasNext()) {
                Comprehension m = iter.next();
                String s = m.getQuestion();
                String st = qs.getText().toString();
                if (s.equals(st)){
                    iter.remove();
                }
            }

            // update db
            markComprehension();

            // show next qs
            display(nextQs());

        }else{

            // make red
            current_selected.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        }

    }

    private void markComprehension() {


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
        DocumentReference ref = db.collection("comprehension").document(current.getId());
        ref.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d("UPDATE-DB-Comprehension","Success!");

                        // update profile
                        updateProfile();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("UPDATE-DB-Comprehension","failure!");
                        Log.d("reason",e.getMessage());
                    }
                });
    }


    private void updateProfile(){

        DocumentReference ref = db.collection("profiles").document(userid);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    // update
                    DocumentSnapshot doc = task.getResult();
                    int comprehensions = Integer.parseInt(doc.getString("comprehensions"));
                    comprehensions++;

                    DocumentReference ref = db.collection("profiles").document(userid);
                    ref.update("comprehensions",String.valueOf(comprehensions))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("FIREBASE-UPD-C-PROFILE","success!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FIREBASE-UPD-C-PROFILE","failed");
                                }
                            });

                }else{
                    Log.d("FIREBASE-UPD-C-PROFILE","failed to fetch user profile");
                }
            }
        });

    }


    private Comprehension nextQs(){
        if (allqs.isEmpty()){
            done();
        }else{
            return allqs.get(new Random().nextInt(allqs.size()));
        }
        return null;
    }
}
