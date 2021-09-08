package com.thisrahul.quantumassignment.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thisrahul.quantumassignment.R;
import com.thisrahul.quantumassignment.databinding.FragmentSignUpBinding;
import com.thisrahul.quantumassignment.helper.ChangePageInterface;
import com.thisrahul.quantumassignment.helper.Utils;
import com.thisrahul.quantumassignment.model.User;

import java.util.UUID;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignUpFragment";
    //binding instance
    private FragmentSignUpBinding binding;
    //firbaseAuth instance
    private FirebaseAuth mAuth;
    // interface instance
    private ChangePageInterface mInterface;
    //progressDialog instance
    private ProgressDialog progressDialog;

    //set interface
    public void setInterface(ChangePageInterface i) {
        mInterface = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        //initialize all views
        initViews();

        return binding.getRoot();
    }

    private void initViews() {

        binding.checkBox.setText("");

        //term and condition setup
        setUpTermConditionChekBox();
        //already acount text setup
        setUpAlreadyAcountTxtView();

        //spinner setup of country code selection start here
        String[] years = {"+91", "+92", "+93"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, years);
        langAdapter.setDropDownViewResource(R.layout.spinner_layout);
        binding.spinner2.setAdapter(langAdapter);
        //end

        //set click listener to signup button
        binding.btnSignUp.setOnClickListener(this);
    }

    private void setUpAlreadyAcountTxtView() {
        String s = "Already have an acount? Sign In!";
        SpannableString ss = new SpannableString(s);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#D60000"));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                mInterface.openSignInFragment();
            }
        };

        ss.setSpan(foregroundColorSpan, 24, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(underlineSpan, 24, 32, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 24, 32, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        binding.txtAlreadyAcount.setText(ss);
        binding.txtAlreadyAcount.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setUpTermConditionChekBox() {
        String s = "I agree with TERMS & CONDITIONS";
        SpannableString ss = new SpannableString(s);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#D60000"));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getContext(), "Term and Condition", Toast.LENGTH_SHORT).show();
            }
        };

        ss.setSpan(foregroundColorSpan, 13, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(underlineSpan, 13, 31, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 13, 31, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        binding.txtCheckBox.setText(ss);
        binding.txtCheckBox.setMovementMethod(LinkMovementMethod.getInstance());
    }


    //create acount
    private void createAccount(String email, String password, String number, String name) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        uploadData(email, password, number, name);
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //   updateUI(null);
                    }
                });
        // [END create_user_with_email]
    }

    //upload data to firebase database
    private void uploadData(String email, String password, String number, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        User user = new User(name, number, email, password, "EmailPassword");

        myRef.child("Users").child("EmailPassword").child(UUID.randomUUID().toString()).setValue(user)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Acount Successfully created!!", Toast.LENGTH_SHORT).show();
                    mInterface.openHomeActivity();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Something is wrong!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //click events
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignUp) {
            if (isValidate()) {
                progressDialog = Utils.progressDialog(getContext());
                progressDialog.show();
                createAccount(binding.etEmailId.getText().toString()
                        , binding.etPassword.getText().toString()
                        , binding.etNumber.getText().toString()
                        , binding.etName.getText().toString());
            }
        }
    }

    //check validation
    private boolean isValidate() {
        if (binding.etName.getText().toString().isEmpty()) {
            binding.etName.setError("Please Enter Valid Name");
            binding.etName.requestFocus();
        } else if (binding.etEmailId.getText().toString().isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmailId.getText().toString()).matches()) {
            binding.etEmailId.setError("Please Enter Valid email id");
            binding.etEmailId.requestFocus();
        } else if (binding.etNumber.getText().toString().isEmpty()
                || binding.etNumber.getText().toString().length() != 10) {
            binding.etNumber.setError("Please Enter Valid Number");
            binding.etNumber.requestFocus();
        } else if (binding.etPassword.getText().toString().isEmpty()
                || binding.etPassword.getText().toString().length() < 6) {
            binding.etPassword.setError("Please Enter Password");
            binding.etPassword.requestFocus();
        } else if (!binding.checkBox.isChecked()) {
            Toast.makeText(getContext(), "Agree to Term and Conditions", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }

        return false;
    }

}