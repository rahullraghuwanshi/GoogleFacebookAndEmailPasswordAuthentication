package com.thisrahul.quantumassignment.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.thisrahul.quantumassignment.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    //binding instance
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //click listener for logout button
        binding.btnLogOut.setOnClickListener(v -> {
            //signout
            FirebaseAuth.getInstance().signOut();
            //open LoginSignUpActivity after  logout
            Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }
}