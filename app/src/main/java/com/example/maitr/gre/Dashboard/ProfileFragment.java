package com.example.maitr.gre.Dashboard;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.maitr.gre.Login.LoginActivity;
import com.example.maitr.gre.R;
import com.example.maitr.gre.Signup.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {


    private Button log;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview =  inflater.inflate(R.layout.fragment_profile, container, false);
        log = (Button) mview.findViewById(R.id.button2);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Toast.makeText(getActivity(), "Succesfully logged out !", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                getActivity().finish();

            }
        });

        return mview;
    }

}
