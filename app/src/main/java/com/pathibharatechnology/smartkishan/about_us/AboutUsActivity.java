package com.pathibharatechnology.smartkishan.about_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

public class AboutUsActivity extends AppCompatActivity {

    TextView aboutUsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        aboutUsText = findViewById(R.id.aboutUsId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "About Us", true);

        aboutUsText.setText("Smart Krishi is a application which includes goods and services offers related to the agriculture. When you start browsing, the app displays an assortment of new goods and services. Buy and sell any agricultural products easily within the app.\n \nSmart Krishi is a mobile marketplace for local buyers and sellers.\n\nBuy or Sell anything, easily post your item for sale by taking a photo.\n\nBrowse agricultural items for sale through app.\n\nMessage, call or request to contact you, buyers and sellers from within the app. \n\nIf you want more information please feel free to contact us at smartkisan7@gmail.com");

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

