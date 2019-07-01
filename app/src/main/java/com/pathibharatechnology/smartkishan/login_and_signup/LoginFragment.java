package com.pathibharatechnology.smartkishan.login_and_signup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pathibharatechnology.smartkishan.MainActivity;
import com.pathibharatechnology.smartkishan.R;


public class LoginFragment extends Fragment {


    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView signUpTextView;
    String email, password;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.emailEditTextID);
        passwordEditText = view.findViewById(R.id.passwordEditTextID);
        loginButton = view.findViewById(R.id.loginButtonID);
        signUpTextView = view.findViewById(R.id.signUpID);
        progressBar = view.findViewById(R.id.progressBarID);

        loginButton.setOnClickListener(new View.OnClickListener() {
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
                            Snackbar.make(getView(), "Login Successful.", Snackbar.LENGTH_LONG)
                                    .show();
                            Toast.makeText(getContext(), "Login successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }else{
                            Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                            Toast.makeText(getContext(), "error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
