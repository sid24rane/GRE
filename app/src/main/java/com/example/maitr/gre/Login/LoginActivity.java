package com.example.maitr.gre.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maitr.gre.Dashboard.DashboardActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button login;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup_link);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = email.getText().toString();
                String user_pwd = password.getText().toString();


                if (TextUtils.isEmpty(user_email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(user_pwd)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging in..");
                progressDialog.setCancelable(false);
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(user_email,user_pwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                    finish();
                                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login failed ! Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
