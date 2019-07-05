package com.pathibharatechnology.smartkishan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pathibharatechnology.smartkishan.login_and_signup.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(FirebaseAuth.getInstance().getUid())) {
            Intent intent = new Intent(this, MainDashboardActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User already logged in...", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            setContentView(R.layout.activity_main);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameForFragmentID, new LoginFragment());
            transaction.commit();

        }
    }
}
