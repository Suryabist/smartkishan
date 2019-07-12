package com.pathibharatechnology.smartkishan;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.about_us.AboutUsActivity;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.new_product.AddNewProductActivity;
import com.pathibharatechnology.smartkishan.product_detail.ProductDetailActivity;
import com.pathibharatechnology.smartkishan.products_list.CategoriesActivity;
import com.pathibharatechnology.smartkishan.products_list.ProductListAdapter;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> listOfCategories =  new ArrayList<>();
    Spinner selectCategorySpinner;
    FloatingActionButton uploadProductFloatingActionButton;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayout nav_header;

    CircleImageView navUserImage;
    TextView navUserName;


    String userId;
    String userProfilePic = null;
    String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uploadProductFloatingActionButton = findViewById(R.id.uploadProductID);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View mView = navigationView.getHeaderView(0);
        nav_header = mView.findViewById(R.id.nav_headerID);
        nav_header.setScrollContainer(true);

        navUserImage = mView.findViewById(R.id.nav_imageID);
        navUserName = mView.findViewById(R.id.nav_userNameID);


        getUserDetails();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



        uploadProductFloatingActionButton = findViewById(R.id.uploadProductID);
        progressBar = findViewById(R.id.progressBarID);
        recyclerView = findViewById(R.id.recyclerViewID);

        SupportActionBarInitializer.setSupportActionBarTitle( this.getSupportActionBar(),"Smart Kishan");


        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        listOfCategories.add("Select a category");
        listOfCategories.add("Fruits");
        listOfCategories.add("Meats and fishes");
        listOfCategories.add("Vegetables");
        listOfCategories.add("Animalistic");
        selectCategorySpinner = findViewById(R.id.searchCategoryID);



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, listOfCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategorySpinner.setAdapter(adapter);



        fetchFeedFromDatabase();







        uploadProductFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainDashboardActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);
                        navUserName.setText(user.getUserName());
                        userName = user.getUserName();
                        userProfilePic = user.getProfilePic();

                        Glide.with(MainDashboardActivity.this).
                                load(user.getProfilePic())
                                .into(navUserImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void fetchFeedFromDatabase(){

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ProductListDTO> productList=new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            productList.add(snap.getValue(ProductListDTO.class));

                        }

                        ProductListAdapter adapter=new ProductListAdapter(productList, MainDashboardActivity.this);

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.profileID) {
            intent = new Intent(MainDashboardActivity.this, UserProfileActivity.class);
            userId = FirebaseAuth.getInstance().getUid();
            intent.putExtra("userId",userId);
            intent.putExtra("userName", userName);
            intent.putExtra("userProfilePic", userProfilePic);

            // Handle the camera action
        } else if (id == R.id.productsID) {

        } else if (id == R.id.uploadProductID) {
            intent = new Intent(MainDashboardActivity.this, AddNewProductActivity.class);


        } else if (id == R.id.categoriesID) {
            intent = new Intent(MainDashboardActivity.this, CategoriesActivity.class);

        } else if (id == R.id.aboutUsID) {
            intent = new Intent(MainDashboardActivity.this, AboutUsActivity.class);

        } else if (id == R.id.logOutID) {
            FirebaseAuth.getInstance().signOut();
            intent = new Intent(MainDashboardActivity.this, MainActivity.class);

        }
        if (intent!=null) {
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
