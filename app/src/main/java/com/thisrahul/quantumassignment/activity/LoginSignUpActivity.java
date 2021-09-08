package com.thisrahul.quantumassignment.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thisrahul.quantumassignment.adapter.LoginSignUpAdapter;
import com.thisrahul.quantumassignment.databinding.ActivityLoginSignupBinding;
import com.thisrahul.quantumassignment.fragment.SignInFragment;
import com.thisrahul.quantumassignment.fragment.SignUpFragment;
import com.thisrahul.quantumassignment.helper.ChangePageInterface;

public class LoginSignUpActivity extends AppCompatActivity implements ChangePageInterface {

    //binding instance
    private ActivityLoginSignupBinding binding;
    //firebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set binding
        binding = ActivityLoginSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //intialize firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //adapter for viewpager
        LoginSignUpAdapter adapter = new LoginSignUpAdapter(getSupportFragmentManager());

        //signUp fragment instance
        SignUpFragment signUpFragment = new SignUpFragment();
        signUpFragment.setInterface(this);
        //signIn fragment instance
        SignInFragment signInFragment = new SignInFragment();
        signInFragment.setInterface(this);

        //add fragments
        adapter.addFragment(signInFragment, "Sign In");
        adapter.addFragment(signUpFragment, "Sign Up");

        //set adapter in viewpager
        binding.viewPager.setAdapter(adapter);
        //set tabs with viewpager
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check user is null or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            openHomeActivity();
        }
    }

    //override method to open signIn fragment
    @Override
    public void openSignInFragment() {
        binding.viewPager.setCurrentItem(0);
    }

    //override method to open signUp fragment
    @Override
    public void openSignUpFragment() {
        binding.viewPager.setCurrentItem(1);
    }

    //override method to open home activty
    @Override
    public void openHomeActivity() {
        Intent intent = new Intent(LoginSignUpActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}