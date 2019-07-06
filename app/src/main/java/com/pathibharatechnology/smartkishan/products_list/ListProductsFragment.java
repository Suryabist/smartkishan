package com.pathibharatechnology.smartkishan.products_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.new_product.AddNewProductFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListProductsFragment extends Fragment {

    ArrayList<String> listOfCategories =  new ArrayList<>();
    Spinner selectCategorySpinner;
    FloatingActionButton uploadProductFloatingActionButton;

    ProgressBar progressBar;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_products, container, false);
        uploadProductFloatingActionButton = view.findViewById(R.id.uploadProductID);
        progressBar = view.findViewById(R.id.progressBarID);
        recyclerView = view.findViewById(R.id.recyclerViewID);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        listOfCategories.add("Select a category");
        listOfCategories.add("Fruits");
        listOfCategories.add("Meats and fishes");
        listOfCategories.add("Vegetables");
        listOfCategories.add("Animalistic");
        selectCategorySpinner = view.findViewById(R.id.searchCategoryID);



        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, listOfCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(adapter);

        uploadProductFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.dashboardFrameID, new AddNewProductFragment());
                fragmentTransaction.commit();
            }
        });

        fetchFeddFromDatabase();

        return view;
    }


    private void fetchFeddFromDatabase(){

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ProductListDTO> postList=new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            postList.add(snap.getValue(ProductListDTO.class));

                        }

                        ProductListAdapter adapter=new ProductListAdapter(postList, getContext());

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
