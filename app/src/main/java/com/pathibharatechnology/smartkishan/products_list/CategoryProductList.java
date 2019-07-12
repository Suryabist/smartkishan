package com.pathibharatechnology.smartkishan.products_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryProductList extends AppCompatActivity {

    String categoryText;
    List<ProductListDTO> productDataList=new ArrayList<>();
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_list);

        Intent intent = getIntent();
        categoryText = intent.getStringExtra("category");

        recyclerView = findViewById(R.id.recyclerViewID);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("products").orderByChild("productCategory").equalTo(categoryText)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ProductListDTO> productList=new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            productList.add(snap.getValue(ProductListDTO.class));

                        }

                        ProductListAdapter productListAdapter = new ProductListAdapter(productList, CategoryProductList.this);
                        recyclerView.setAdapter(productListAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
