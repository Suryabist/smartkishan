package com.pathibharatechnology.smartkishan.products_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoriesActivity extends AppCompatActivity {

    ArrayList<String> categoryArrayList = new ArrayList<>();
    HashMap<String, String> categoryImageData = new HashMap<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Categories", true);

        recyclerView = findViewById(R.id.recyclerViewID);

        categoryArrayList.add("fruits");
        categoryImageData.put("fruits", "Fruits");
        categoryArrayList.add("meats_and_fishes");
        categoryImageData.put("meats_and_fishes", "Meats and fishes");
        categoryArrayList.add("vegetables");
        categoryImageData.put("vegetables", "Vegetables");
        categoryArrayList.add("animalistic");
        categoryImageData.put("animalistic", "Animalistic");

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);


        CategoryAdapter adapter=new CategoryAdapter(categoryArrayList, categoryImageData, CategoriesActivity.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CategoriesActivity.this, MainDashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
