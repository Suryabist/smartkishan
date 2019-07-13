package com.pathibharatechnology.smartkishan.product_detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.notification_package.NotificationDTO;
import com.pathibharatechnology.smartkishan.notification_package.NotificationList;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailActivity extends AppCompatActivity {


    ImageView productImageView;
    TextView nameOfProduct, priceTextview, productDescriptionTextView, uploaderUserNameTextview, productDeliveryTextview;
    CircleImageView uploaderImage;
    FloatingActionButton editProductInfoFloatingButton, callFloatingButton;
    LinearLayout uploaderDetailLinearLayout;
    Button sendNotificationButton;

    String userId, uploaderUserName, imageUrl, productName, productDetail, deliveryLocation, uploaderImageUrl, productId, productUploaderUserId;
    Integer price;
    String currentUserName;

    String mobile;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImageView = findViewById(R.id.productImageviewID);
        nameOfProduct = findViewById(R.id.nameOfProductID);
        priceTextview = findViewById(R.id.priceID);
        productDescriptionTextView = findViewById(R.id.descriptionOfProductID);
        uploaderUserNameTextview = findViewById(R.id.uploadedByID);
        productDeliveryTextview = findViewById(R.id.placeID);
        uploaderImage = findViewById(R.id.uploaderIamgeID);
        editProductInfoFloatingButton = findViewById(R.id.editProductInfoID);
        uploaderDetailLinearLayout = findViewById(R.id.uploaderDetailID);
        sendNotificationButton = findViewById(R.id.notifyID);
        callFloatingButton = findViewById(R.id.callFloatingButtonID);

        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        uploaderUserName = intent.getStringExtra("uploaderUserName");
        imageUrl = intent.getStringExtra("imageUrl");
        productName = intent.getStringExtra("productName");
        price = intent.getIntExtra("productPrice", 0);
        productDetail = intent.getStringExtra("productDetail");
        deliveryLocation = intent.getStringExtra("deliveryLocation");
        uploaderImageUrl = intent.getStringExtra("uploaderImageUrl");
        productId = intent.getStringExtra("productId");
        productUploaderUserId = intent.getStringExtra("productUploaderId");

        getUserDetails();

        nameOfProduct.setText(productName);
        priceTextview.setText(" NRs. " + price);
        System.out.println("Product description====" + productDetail);
        productDescriptionTextView.setText(productDetail + " ");
        uploaderUserNameTextview.setText(uploaderUserName);
        productDeliveryTextview.setText(deliveryLocation);

        Glide.with(this)
                .load(imageUrl).asBitmap().into(productImageView);
        Glide.with(this)
                .load(uploaderImageUrl).asBitmap().into(uploaderImage);

        if (FirebaseAuth.getInstance().getUid().equals(userId)) {
            editProductInfoFloatingButton.setVisibility(View.VISIBLE);
        }

        getUploaderPhone(productUploaderUserId);


        callFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (ActivityCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ProductDetailActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 0);

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    System.out.println("mobile====="+mobile);
                    callIntent.setData(Uri.parse("tel:" + mobile));
                    ProductDetailActivity.this.startActivity(callIntent);
                }

            }
        });

        uploaderDetailLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userDetailIntent = new Intent(ProductDetailActivity.this, UserProfileActivity.class);
                userDetailIntent.putExtra("userId", userId);
                userDetailIntent.putExtra("userName", uploaderUserName);
                userDetailIntent.putExtra("userProfilePic", uploaderImageUrl);
                startActivity(userDetailIntent);
                finish();
            }
        });

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationDTO notificationDTO = new NotificationDTO();
                String notificationId = FirebaseDatabase.getInstance().getReference().child("notifications").push().getKey();

                DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                Date date = new Date();
                String strDate = dateFormat.format(date);

                notificationDTO.setNotificationId(notificationId);
                notificationDTO.setNotificationSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                notificationDTO.setProductId(productId);
                notificationDTO.setProductName(productName);
                notificationDTO.setStatusRead(false);
                notificationDTO.setCreatedDate(strDate);
                notificationDTO.setProductUploaderUserId(productUploaderUserId);
                notificationDTO.setNotificationSenderName(currentUserName);
                uploadNotification(notificationDTO);


            }
        });


    }

    private void getUploaderPhone(String uploaderUserId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uploaderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);
                        mobile = user.getMobile();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void uploadNotification(NotificationDTO notificationDTO) {
        FirebaseDatabase.getInstance().getReference().child("notifications")
                .child(notificationDTO.getNotificationId())
                .setValue(notificationDTO)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getWindow().getDecorView().getRootView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
//                            onBackPressed();
                            Toast.makeText(ProductDetailActivity.this, "Notification sent successfully...", Toast.LENGTH_SHORT).show();
                            /*Intent intent = new Intent(Pro.this, MainDashboardActivity.class);
                            startActivity(intent);
                            finish();*/
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
                        currentUserName = user.getUserName();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
