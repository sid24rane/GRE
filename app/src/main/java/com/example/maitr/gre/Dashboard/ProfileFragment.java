package com.example.maitr.gre.Dashboard;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.Login.LoginActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {


    private Button log;
    private TextView comprehensions;
    private TextView meaning;
    private TextView dictionary;
    private TextView echo;
    private TextView jumbledwords;
    private FirebaseFirestore db;
    private TextView name;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View mview =  inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();

        log = (Button) mview.findViewById(R.id.button2);
        comprehensions = (TextView) mview.findViewById(R.id.progressComprehension);
        meaning = (TextView) mview.findViewById(R.id.progressMeaning);
        dictionary = (TextView) mview.findViewById(R.id.progressDictionary);
        jumbledwords = (TextView) mview.findViewById(R.id.progressJumbled);
        name = (TextView) mview.findViewById(R.id.name);
        echo = (TextView) mview.findViewById(R.id.progressEcho);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(getActivity(), "Successfully logged out !", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                getActivity().finish();

            }
        });

        load();

        return mview;
    }

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading profile please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference ref = db.collection("profiles").document(userid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        DocumentSnapshot doc = task.getResult();

                        // get user current scores
                        int meaning_score = Integer.parseInt(doc.getString("guess_meaning"));
                        int comprehensions_score = Integer.parseInt(doc.getString("comprehensions"));
                        int dictionary_score = Integer.parseInt(doc.getString("dictionary"));
                        int jumbled_words_score = Integer.parseInt(doc.getString("jumbled_words"));
                        int echo_score = Integer.parseInt(doc.getString("echo"));

                        String username = doc.getString("name");

                        // get totals of each exercies
                        String ctotal = getData("comprehension");
                        String mtotal = getData("guess_meaning");
                        String dtotal = getData("words");
                        String jtotal = getData("jumbled_words");
                        String etotal = getData("echo");

                        //set text
                        comprehensions.setText("Comprehension : " + comprehensions_score + " / " + ctotal);
                        dictionary.setText("Dictionary : " + dictionary_score + " / " + dtotal);
                        jumbledwords.setText("Jumbled Words : " + jumbled_words_score + " / " + jtotal);
                        meaning.setText("Meaning : " + meaning_score + " / " + mtotal);
                        name.setText("Hello, " + username);
                        echo.setText("Echo : " + echo_score + " / " + etotal);

                        progressDialog.dismiss();

                    }else{
                        Log.d("FIREBASE-PROFILE-NO","failed");
                    }
            }
        });

    }

    private String getData(String type){

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("total", 0); // 0 - for private mode
        return pref.getString(type,null);

    }

}
