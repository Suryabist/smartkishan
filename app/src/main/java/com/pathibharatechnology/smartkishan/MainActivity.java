package com.pathibharatechnology.smartkishan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.pathibharatechnology.smartkishan.login_and_signup.LoginFragment;
import com.pathibharatechnology.smartkishan.login_and_signup.SignUpFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .add(R.id.frameForFragmentID, new LoginFragment());
        transaction.commit();
    }
}
