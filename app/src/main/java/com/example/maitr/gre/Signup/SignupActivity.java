package com.example.maitr.gre.Signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maitr.gre.Dashboard.DashboardActivity;
import com.example.maitr.gre.Login.LoginActivity;
import com.example.maitr.gre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private Button signup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        init();

    }

    private void init() {


        name = (EditText) findViewById(R.id.signup_name);
        email = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        signup = (Button) findViewById(R.id.signup);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String user_email = email.getText().toString();
                String user_pwd = password.getText().toString();
                String user_name = name.getText().toString();

                if (TextUtils.isEmpty(user_email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(user_pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(user_name)){
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (user_pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setMessage("Registering please wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(user_email,user_pwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    String userid = task.getResult().getUser().getUid();
                                    createProfile(userid);
                                    progressDialog.dismiss();
                                    Intent i = new Intent(SignupActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(SignupActivity.this, "Signup failed ! Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void createProfile(String userid) {


        Map<String,Object> profile = new HashMap<>();
        profile.put("dictionary",String.valueOf(0));
        profile.put("comprehensions",String.valueOf(0));
        profile.put("guess_meaning",String.valueOf(0));
        profile.put("jumbled_words",String.valueOf(0));
        profile.put("name",name.getText().toString());
        profile.put("echo",String.valueOf(0));

        //create profile
        db.collection("profiles")
                .document(userid)
                .set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FIREBASE-PROFILE","created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FIREBASE-PROFILE","creation failed!");
                    }
                });

    }

}
