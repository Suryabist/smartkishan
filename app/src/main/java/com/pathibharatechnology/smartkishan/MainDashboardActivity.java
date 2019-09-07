package com.pathibharatechnology.smartkishan;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.about_us.AboutUsActivity;
import com.pathibharatechnology.smartkishan.job.JobActivity;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.new_product.AddNewProductActivity;
import com.pathibharatechnology.smartkishan.notification_package.NotificationAdapter;
import com.pathibharatechnology.smartkishan.notification_package.NotificationList;
import com.pathibharatechnology.smartkishan.notification_package.NotificationDTO;
import com.pathibharatechnology.smartkishan.product_by_category.ProductByCategoryActivity;
import com.pathibharatechnology.smartkishan.products_list.CategoriesActivity;
import com.pathibharatechnology.smartkishan.product_by_category.ProductListAdapter;
import com.pathibharatechnology.smartkishan.product_by_category.ProductListDTO;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MainDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton uploadProductFloatingActionButton;
    ProgressBar progressBar;
    LinearLayout nav_header;
    CircleImageView navUserImage;
    TextView navUserName;
    String userId;
    String userProfilePic = null;
    String userName = null;
    ImageView notificationImage;
    TextView notificationCountTextview;

    RecyclerView fruitsRecyclerView, fishAndMeatRecyclerView, vegetablesRecyclerView, animalistciRecyclerView, grainsRecyclerView;
    LinearLayoutManager linearLayoutManager;
    ProductListAdapter adapter;
    LinearLayout fruitsLayout, fishMeatlayout, vegetablesLayout, animalisticsLayout, grainsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBarID);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View mView = navigationView.getHeaderView(0);
        nav_header = mView.findViewById(R.id.nav_headerID);
        nav_header.setScrollContainer(true);

        navUserImage = mView.findViewById(R.id.nav_imageID);
        navUserName = mView.findViewById(R.id.nav_userNameID);

        try {
            getUserDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Menu menu = navigationView.getMenu();
        MenuItem logInMenu = menu.findItem(R.id.loginId);
        MenuItem logOutMenu = menu.findItem(R.id.logOutID);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            logInMenu.setVisible(false);
            logOutMenu.setVisible(true);
        } else {
            logInMenu.setVisible(true);
            logOutMenu.setVisible(false);
        }

        defineView();

        callLinearLayoutManager(fruitsRecyclerView);
        callLinearLayoutManager(fishAndMeatRecyclerView);
        callLinearLayoutManager(vegetablesRecyclerView);
        callLinearLayoutManager(animalistciRecyclerView);
        callLinearLayoutManager(grainsRecyclerView);

        getProductBycategoryTask("फलफुल", fruitsRecyclerView, fruitsLayout);
        getProductBycategoryTask("माछा मासु", fishAndMeatRecyclerView, fishMeatlayout);
        getProductBycategoryTask("तरकारी", vegetablesRecyclerView, vegetablesLayout);
        getProductBycategoryTask("पशुजन्य", animalistciRecyclerView, animalisticsLayout);
        getProductBycategoryTask("अन्न", grainsRecyclerView, grainsLayout);

        navigationView.setNavigationItemSelectedListener(this);

        uploadProductFloatingActionButton = findViewById(R.id.uploadProductID);

        SupportActionBarInitializer.setSupportActionBarTitle(this.getSupportActionBar(), "Smart Kishan");

        uploadProductFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(MainDashboardActivity.this, AddNewProductActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainDashboardActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void getUserDetails() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
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

    private void defineView() {
        fruitsRecyclerView = findViewById(R.id.fruitsRecyclerViewId);
        fishAndMeatRecyclerView = findViewById(R.id.fishesAndMeatRecyclerViewId);
        vegetablesRecyclerView = findViewById(R.id.vegetablesRecyclerId);
        animalistciRecyclerView = findViewById(R.id.animalisticRecyclerViewId);
        grainsRecyclerView = findViewById(R.id.grainsRecyclerViewId);

        fruitsLayout = findViewById(R.id.fruitLayoutId);
        fishMeatlayout = findViewById(R.id.fishMeatLayoutId);
        vegetablesLayout = findViewById(R.id.vegetablesLayoutId);
        animalisticsLayout = findViewById(R.id.animalisticLayoutId);
        grainsLayout = findViewById(R.id.grainsLayoutId);
    }

    private void callLinearLayoutManager(RecyclerView recyclerView) {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }


    public void getProductBycategoryTask(String categoryText, final RecyclerView recyclerView, final LinearLayout linearLayout){

        progressBar.setVisibility(View.VISIBLE);

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
                        adapter= new ProductListAdapter(productList, MainDashboardActivity.this);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);

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

        MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View showNotificationView = menuItem.getActionView();

        notificationImage = showNotificationView.findViewById(R.id.notificationImageID);
        notificationCountTextview = showNotificationView.findViewById(R.id.notificationCountID);
        notificationCountTextview.setVisibility(View.INVISIBLE);
        getNotifications();

        notificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(MainDashboardActivity.this, NotificationList.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainDashboardActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        return true;
    }


    public void getNotifications() {

        FirebaseDatabase.getInstance().getReference().child("notifications").orderByChild("productUploaderUserId").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<NotificationDTO> notificationDTOList = new ArrayList<>();
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot snap = iterator.next();
                            notificationDTOList.add(snap.getValue(NotificationDTO.class));
                        }

                        NotificationAdapter notificationAdapter = new NotificationAdapter(notificationDTOList, MainDashboardActivity.this);

                        int notificationCount = notificationAdapter.performFiltering();
                        setUpBadge(notificationCount);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setUpBadge(int notificationCount) {
        if (notificationCount == 0) {
            notificationCountTextview.setVisibility(View.GONE);
        } else {
            notificationCountTextview.setVisibility(View.VISIBLE);
            notificationCountTextview.setText(notificationCount + "");
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent = null;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();


            if (id == R.id.profileID) {
                intent = new Intent(MainDashboardActivity.this, UserProfileActivity.class);
                userId = FirebaseAuth.getInstance().getUid();
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                intent.putExtra("userProfilePic", userProfilePic);

                // Handle the camera action
            } else if (id == R.id.productsID) {

            } else if (id == R.id.jobsId) {
                intent = new Intent(MainDashboardActivity.this, JobActivity.class);

            } else if (id == R.id.uploadProductID) {
                intent = new Intent(MainDashboardActivity.this, AddNewProductActivity.class);


            } else if (id == R.id.categoriesID) {
                intent = new Intent(MainDashboardActivity.this, CategoriesActivity.class);

            } else if (id == R.id.aboutUsID) {
                intent = new Intent(MainDashboardActivity.this, AboutUsActivity.class);

            } else if (id == R.id.logOutID) {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(MainDashboardActivity.this, MainActivity.class);
                LoginManager.getInstance().logOut();
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            }

            if (intent != null) {
                startActivity(intent);
            }
        } else {
            intent = new Intent(MainDashboardActivity.this, MainActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}