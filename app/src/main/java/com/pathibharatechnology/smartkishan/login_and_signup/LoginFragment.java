package com.pathibharatechnology.smartkishan.login_and_signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.pathibharatechnology.smartkishan.MainActivity;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static com.facebook.AccessTokenManager.TAG;


public class LoginFragment extends Fragment {


    EditText emailEditText, passwordEditText;
    Button signInButton;
    TextView signUpTextView;
    String email, password;
    ProgressBar progressBar;
    LoginButton fbLoginButton;

    CallbackManager callbackManager;
    private static final String EMAIL = "email";

    FirebaseAuth mAuth;
    StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.emailEditTextID);
        passwordEditText = view.findViewById(R.id.passwordEditTextID);
        signInButton = view.findViewById(R.id.signInButtonId);
        signUpTextView = view.findViewById(R.id.signUpTextviewId);
        progressBar = view.findViewById(R.id.progressBarID);

        fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));


        fbLoginButton.setFragment(this);

        mAuth = FirebaseAuth.getInstance();

        // Callback registration

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

                // App code
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Text cancel", Toast.LENGTH_SHORT).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getContext(), "Text error: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    loginUserToFirebase();
                    // Toast.makeText(LoginActivity.this, "validated input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.frameForFragmentID, new SignUpFragment());
                fragmentTransaction.commit();
            }
        });






        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void handleFacebookAccessToken(AccessToken token) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            uploadUserInformation();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }

                    }
                });
    }

    private void uploadUserInformation()
    {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        UserDTO user =new UserDTO();
        user.setFullName(currentUser.getDisplayName());
        String email;
        if (currentUser.getEmail() != null){
            email = currentUser.getEmail();
        } else {
            email = "";
        }
        user.setEmail(email);
        user.setUserName(currentUser.getDisplayName());
        String phone;
        if (currentUser.getPhoneNumber() != null){
            phone = currentUser.getPhoneNumber();
        } else {
            phone = "";
        }
        user.setMobile(phone);
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        user.setJoinedTime(strDate);
        user.setProfilePic(currentUser.getPhotoUrl().toString());



        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Snackbar.make(getView(), "Facebook login successful.", Snackbar.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                })
        ;

    }



    public void updateUI() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Toast.makeText(getContext(), "Logged in surya.", Toast.LENGTH_SHORT).show();
        }
    }


    /*@Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Toast.makeText(getContext(), "Hello surya. logged in to fb", Toast.LENGTH_SHORT).show();
        }
    }*/

    private boolean validate(){
        boolean isValid=false;
        email=emailEditText.getText().toString();
        password=passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Required");
        }else if(TextUtils.isEmpty(password)){
            passwordEditText.setError("Required");
        }else if(password.length()<8){
            passwordEditText.setError("Should be of minimum 8 characters");

        }else{
            isValid=true;

        }
        return isValid;
    }


    private void loginUserToFirebase(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                Snackbar.make(getView(), "Login Successful.", Snackbar.LENGTH_LONG)
                                        .show();
                                Toast.makeText(getContext(), "Login successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Please verify your email...", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                            Toast.makeText(getContext(), "error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
