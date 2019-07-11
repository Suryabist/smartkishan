package com.pathibharatechnology.smartkishan.user_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.products_list.ProductListAdapter;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    CircleImageView userProfilePicImageview;
    FloatingActionButton editProfileFloatingAction;
    TextView joinedDateTextview, itemCountTextview;
    String userId, userName, userProfilePic;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarID);
        userProfilePicImageview = findViewById(R.id.userProfileImageID);
        editProfileFloatingAction = findViewById(R.id.editUserDetailID);
        joinedDateTextview = findViewById(R.id.joinDateTextviewId);
        itemCountTextview = findViewById(R.id.itemCountTextViewID);
        recyclerView = findViewById(R.id.recyclerViewID);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userProfilePic = intent.getStringExtra("userProfilePic");

        collapsingToolbarLayout.setTitle(userName);
        Glide.with(this)
                .load(userProfilePic)
                .asBitmap()
                .into(userProfilePicImageview);

        getUserDetails();
        getUserProducts();


    }


    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);
                        joinedDateTextview.setText("Joined on "+user.getJoinedTime());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void getUserProducts(){
        FirebaseDatabase.getInstance().getReference().child("products")
                .orderByChild("productUploaderUserId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ProductListDTO> productListDTOS=new ArrayList<>();
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()){
                            DataSnapshot snap=iterator.next();
                            productListDTOS.add(snap.getValue(ProductListDTO.class));

                        }
                        itemCountTextview.setText("Uploaded items: "+productListDTOS.size());

                        ProductListAdapter adapter=new ProductListAdapter(productListDTOS, UserProfileActivity.this);

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserProfileActivity.this, MainDashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
