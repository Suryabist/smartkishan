package com.pathibharatechnology.smartkishan.product_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailActivity extends AppCompatActivity {


    ImageView productImageView;
    TextView nameOfProduct, priceTextview, productDescriptionTextView, uploaderUserNameTextview, productDeliveryTextview;
    CircleImageView uploaderImage;
    FloatingActionButton editProductInfoFloatingButton;
    LinearLayout uploaderDetailLinearLayout;

    String userId, uploaderUserName, imageUrl, productName, productDetail, deliveryLocation, uploaderImageUrl;
    Integer price;

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

        final Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        uploaderUserName = intent.getStringExtra("uploaderUserName");
        imageUrl = intent.getStringExtra("imageUrl");
        productName = intent.getStringExtra("productName");
        price = intent.getIntExtra("productPrice", 0);
        productDetail = intent.getStringExtra("productDetail");
        deliveryLocation = intent.getStringExtra("deliveryLocation");
        uploaderImageUrl = intent.getStringExtra("uploaderImageUrl");

        nameOfProduct.setText(productName);
        priceTextview.setText(" NRs. "+price);
        System.out.println("Product description===="+productDetail);
        productDescriptionTextView.setText(productDetail+" ");
        uploaderUserNameTextview.setText(uploaderUserName);
        productDeliveryTextview.setText(deliveryLocation);

        Glide.with(this)
                .load(imageUrl).asBitmap().into(productImageView);
        Glide.with(this)
                .load(uploaderImageUrl).asBitmap().into(uploaderImage);

        if (FirebaseAuth.getInstance().getUid().equals(userId)) {
            editProductInfoFloatingButton.setVisibility(View.VISIBLE);
        }

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


    }
}
