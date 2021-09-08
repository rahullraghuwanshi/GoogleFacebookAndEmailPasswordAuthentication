package com.thisrahul.quantumassignment.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thisrahul.quantumassignment.R;
import com.thisrahul.quantumassignment.databinding.FragmentSignInBinding;
import com.thisrahul.quantumassignment.helper.ChangePageInterface;
import com.thisrahul.quantumassignment.helper.Utils;
import com.thisrahul.quantumassignment.model.User;

import java.util.Arrays;
import java.util.UUID;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignInFragment";
    private static final int RC_SIGN_IN = 9001;

    //binding instance
    private FragmentSignInBinding binding;
    //firebase auth instance
    private FirebaseAuth mAuth;
    //insterface instance
    private ChangePageInterface mInterface;
    //progressDialog instance
    private ProgressDialog progressDialog;
    //GoogleSignInClient instance
    private GoogleSignInClient mGoogleSignInClient;
    //CallbackManager instance
    private CallbackManager mCallbackManager;

    //set interface
    public void setInterface(ChangePageInterface i) {
        mInterface = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //binding
        binding = FragmentSignInBinding.inflate(inflater, container, false);

        //google signin options initialisation
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //googlesign in client initialisation
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        //facebook
        // Initialize Facebook Login button
        FacebookSdk.sdkInitialize(getContext());
        mCallbackManager = CallbackManager.Factory.create();

        //firebase auth initialize
        mAuth = FirebaseAuth.getInstance();

        //initialize all views
        initViews();

        return binding.getRoot();
    }

    private void initViews() {

        //set click listener to the buttons
        binding.btnSignIn.setOnClickListener(this);
        binding.txtForgotPassword.setOnClickListener(this);
        binding.imgFacebook.setOnClickListener(this);
        binding.imgGoogle.setOnClickListener(this);

        //initialise progressDialog
        progressDialog = Utils.progressDialog(getContext());


        String s = "Don't have an acount? Register now";
        SpannableString ss = new SpannableString(s);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#D60000"));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                mInterface.openSignUpFragment();
            }
        };

        ss.setSpan(foregroundColorSpan, 22, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(underlineSpan, 22, 34, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 22, 34, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        //set spannable text to dont have acount
        binding.txtDontAcount.setText(ss);
        binding.txtDontAcount.setMovementMethod(LinkMovementMethod.getInstance());

    }

    //check all validation
    private boolean isValidate() {
        if (binding.etEmailId.getText().toString().isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmailId.getText().toString()).matches()) {
            binding.etEmailId.setError("Please Enter Valid email id");
            binding.etEmailId.requestFocus();
        } else if (binding.etPassword.getText().toString().isEmpty()
                || binding.etPassword.getText().toString().length() < 6) {
            binding.etPassword.setError("Please Enter Password");
            binding.etPassword.requestFocus();
        } else {
            return true;
        }

        return false;
    }

    //click events for buttons
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSignIn) {
            if (isValidate()) {
                progressDialog.show();
                signIn(binding.etEmailId.getText().toString(), binding.etPassword.getText().toString());
            }
        }

        if (v.getId() == R.id.txtForgotPassword) {
            Toast.makeText(getContext(), "Forgot Password!!", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.imgFacebook) {
            facebookSignIn();
        }

        if (v.getId() == R.id.imgGoogle) {
            googleSignIn();
        }


    }

    //google signin
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //sign In with email and password
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Sign In Succesfully", Toast.LENGTH_SHORT).show();
                        mInterface.openHomeActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Something is wrong!!", Toast.LENGTH_SHORT).show();
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            uploadData(acount.getEmail(), acount.getDisplayName(), "Google");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Something is wrong!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //upload data to firebase database
    private void uploadData(String email, String name, String signInMethod) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        User user = new User(name, email, signInMethod);


        myRef.child("Users").child(signInMethod).child(UUID.randomUUID().toString()).setValue(user)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Acount Successfully created!!", Toast.LENGTH_SHORT).show();
                    mInterface.openHomeActivity();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Something is wrong!!", Toast.LENGTH_SHORT).show();
                });
    }

    //facebook signIn
    private void facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(SignInFragment.this
                , Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    //handle facebook access token
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadData(user.getEmail(), user.getDisplayName(), "Facebook");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}