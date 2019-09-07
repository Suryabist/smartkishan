package com.pathibharatechnology.smartkishan.product_by_category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.products_list.CategoryProductList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProductByCategoryActivity extends AppCompatActivity {

    RecyclerView fruitsRecyclerView, fishAndMeatRecyclerView, vegetablesRecyclerView;
    LinearLayoutManager linearLayoutManager;
    DividerItemDecoration mDividerItemDecoration;
    ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_category);

        fruitsRecyclerView = findViewById(R.id.fruitsRecyclerViewId);
        fishAndMeatRecyclerView = findViewById(R.id.fishesAndMeatRecyclerViewId);
        vegetablesRecyclerView = findViewById(R.id.vegetablesRecyclerId);


        linearLayoutManager = new LinearLayoutManager(this);
        /*mDividerItemDecoration = new DividerItemDecoration(fruitsRecyclerView.getContext(),
                linearLayoutManager.HORIZONTAL);
        fruitsRecyclerView.addItemDecoration(mDividerItemDecoration);
        fruitsRecyclerView.setNestedScrollingEnabled(true);
        fruitsRecyclerView.setLayoutFrozen(true);*/
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        fruitsRecyclerView.setLayoutManager(linearLayoutManager);


        linearLayoutManager = new LinearLayoutManager(this);
        /*mDividerItemDecoration = new DividerItemDecoration(fishAndMeatRecyclerView.getContext(),
                linearLayoutManager.HORIZONTAL);
        fishAndMeatRecyclerView.addItemDecoration(mDividerItemDecoration);
        fishAndMeatRecyclerView.setNestedScrollingEnabled(true);
        fishAndMeatRecyclerView.setLayoutFrozen(true);*/

        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        fishAndMeatRecyclerView.setLayoutManager(linearLayoutManager);

        linearLayoutManager = new LinearLayoutManager(this);
        /*mDividerItemDecoration = new DividerItemDecoration(vegetablesRecyclerView.getContext(),
                linearLayoutManager.HORIZONTAL);
        vegetablesRecyclerView.addItemDecoration(mDividerItemDecoration);
        vegetablesRecyclerView.setNestedScrollingEnabled(true);
        vegetablesRecyclerView.setLayoutFrozen(true);*/
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        vegetablesRecyclerView.setLayoutManager(linearLayoutManager);

        /*getProductBycategoryTask("fruits", fruitsRecyclerView);
        getProductBycategoryTask("meat_fish", fishAndMeatRecyclerView);
        getProductBycategoryTask("vegetables", vegetablesRecyclerView);*/

        getProductBycategoryTask("फलफुल", fruitsRecyclerView);
        getProductBycategoryTask("माछा मासु", fishAndMeatRecyclerView);
        getProductBycategoryTask("तरकारी", vegetablesRecyclerView);



    }



    public void getProductBycategoryTask(String categoryText, final RecyclerView recyclerView){

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

                        adapter= new ProductListAdapter(productList, ProductByCategoryActivity.this);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

}
