package com.pathibharatechnology.smartkishan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.pathibharatechnology.smartkishan.login.LoginFragment;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(FirebaseAuth.getInstance().getUid())) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                Intent intent = new Intent(this, MainDashboardActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else {

            setContentView(R.layout.activity_main);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameForFragmentID, new LoginFragment());
            transaction.commit();

        }
    }
}
