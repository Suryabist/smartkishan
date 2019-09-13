package com.pathibharatechnology.smartkishan.product_by_category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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

    RecyclerView fruitsRecyclerView, fishAndMeatRecyclerView, vegetablesRecyclerView, animalistciRecyclerView, grainsRecyclerView, dairyRecyclerView, othersRecyclerView;
    LinearLayoutManager linearLayoutManager;
    DividerItemDecoration mDividerItemDecoration;
    ProductListAdapter adapter;
    LinearLayout fruitsLayout, fishMeatlayout, vegetablesLayout, animalisticsLayout, grainsLayout, dairyLayout, othersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_category);

        defineView();

        callLinearLayoutManager(fruitsRecyclerView);
        callLinearLayoutManager(fishAndMeatRecyclerView);
        callLinearLayoutManager(vegetablesRecyclerView);
        callLinearLayoutManager(animalistciRecyclerView);
        callLinearLayoutManager(grainsRecyclerView);
        callLinearLayoutManager(dairyRecyclerView);
        callLinearLayoutManager(othersRecyclerView);

        getProductBycategoryTask("फलफुल", fruitsRecyclerView, fruitsLayout);
        getProductBycategoryTask("माछा मासु", fishAndMeatRecyclerView, fishMeatlayout);
        getProductBycategoryTask("तरकारी", vegetablesRecyclerView, vegetablesLayout);
        getProductBycategoryTask("पशुजन्य", animalistciRecyclerView, animalisticsLayout);
        getProductBycategoryTask("अन्न", grainsRecyclerView, grainsLayout);
        getProductBycategoryTask("दुग्ध", dairyRecyclerView, dairyLayout);
        getProductBycategoryTask("अन्य", othersRecyclerView, othersLayout);

    }

    private void defineView() {
        fruitsRecyclerView = findViewById(R.id.fruitsRecyclerViewId);
        fishAndMeatRecyclerView = findViewById(R.id.fishesAndMeatRecyclerViewId);
        vegetablesRecyclerView = findViewById(R.id.vegetablesRecyclerId);
        animalistciRecyclerView = findViewById(R.id.animalisticRecyclerViewId);
        grainsRecyclerView = findViewById(R.id.grainsRecyclerViewId);
        dairyRecyclerView = findViewById(R.id.dairyRecyclerViewId);
        othersRecyclerView = findViewById(R.id.othersRecyclerViewId);

        fruitsLayout = findViewById(R.id.fruitLayoutId);
        fishMeatlayout = findViewById(R.id.fishMeatLayoutId);
        vegetablesLayout = findViewById(R.id.vegetablesLayoutId);
        animalisticsLayout = findViewById(R.id.animalisticLayoutId);
        grainsLayout = findViewById(R.id.grainsLayoutId);
        dairyLayout = findViewById(R.id.dairyLayoutId);
        othersLayout = findViewById(R.id.othersLayoutId);
    }

    private void callLinearLayoutManager(RecyclerView recyclerView) {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }


    public void getProductBycategoryTask(String categoryText, final RecyclerView recyclerView, final LinearLayout linearLayout){

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

                        if (!productList.isEmpty()) {
                            linearLayout.setVisibility(View.VISIBLE);
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
