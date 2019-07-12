package com.pathibharatechnology.smartkishan.products_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CategoryProductList extends AppCompatActivity {

    String categoryText;
    List<ProductListDTO> productDataList=new ArrayList<>();
    RecyclerView recyclerView;

    ProductListAdapter productListAdapter;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_list);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Products", true);

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

                         productListAdapter= new ProductListAdapter(productList, CategoryProductList.this);
                        recyclerView.setAdapter(productListAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
