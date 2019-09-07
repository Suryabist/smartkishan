package com.pathibharatechnology.smartkishan.product_detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.pathibharatechnology.smartkishan.new_product.AddNewProductActivity;
import com.pathibharatechnology.smartkishan.notification_package.NotificationDTO;
import com.pathibharatechnology.smartkishan.product_by_category.ProductListDTO;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class ProductDetailActivity extends AppCompatActivity {


    ImageView productImageView;
    TextView nameOfProduct, priceTextview, productDescriptionTextView, uploaderUserNameTextview, productDeliveryTextview;
    CircleImageView uploaderImage;
    FloatingActionButton editProductInfoFloatingButton, callFloatingButton, deleteFloatingButton;
    LinearLayout uploaderDetailLinearLayout;
    Button sendNotificationButton;

    String userId, uploaderUserName, imageUrl, productName, productDetail, deliveryLocation, uploaderImageUrl, productId, productUploaderUserId;
    Integer price;
    String currentUserName;

    ImageButton sendMailButton;
    String mobile, clientMobile;
    String email;

    boolean deleteProductResponse = false;

    String productCategory, productPrice;

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
        sendMailButton = findViewById(R.id.sendMailButtonID);
        deleteFloatingButton = findViewById(R.id.deleteProductId);

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
        getProductDetails(productId);

        nameOfProduct.setText(productName);
        priceTextview.setText(price + " ने.रू.");
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
            deleteFloatingButton.setVisibility(View.VISIBLE);
        }


        editProductInfoFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getEditIntent = new Intent(ProductDetailActivity.this, AddNewProductActivity.class);
                getEditIntent.putExtra("productImageUrl", imageUrl);
                getEditIntent.putExtra("productName", productName);
                getEditIntent.putExtra("productCategory", productCategory);
                getEditIntent.putExtra("productPrice", productPrice);
                getEditIntent.putExtra("productDetail", productDetail);
                getEditIntent.putExtra("productDeliveryLocation", deliveryLocation);
                getEditIntent.putExtra("productId", productId);
                startActivity(getEditIntent);
            }
        });

        deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProductDetailActivity.this, "Delete button", Toast.LENGTH_SHORT).show();


                new AlertDialog.Builder(ProductDetailActivity.this)
                        .setTitle("पुष्टि गर्नुहोस्")
                        .setMessage("के तपाई वास्तवमै यस उत्पादनलाई मेटाउन चाहानुहुन्छ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("हटाउनुहोस्", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DeleteProductTask deleteProductTask = new DeleteProductTask();
                                deleteProductTask.deleteProduct(productId, ProductDetailActivity.this);
                                dialog.dismiss();
                                finish();


                            }
                        })
                        .setNegativeButton("नहटाउनुहोस्", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ProductDetailActivity.this, "उत्पादन मेटिएको छैन।", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        }).show();


            }
        });


        getUploaderPhone(productUploaderUserId);


        sendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMailToUploader();
            }
        });


        callFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (ActivityCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ProductDetailActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, 0);

                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    System.out.println("mobile=====" + mobile);
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

    private void getUploaderPhone(final String uploaderUserId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(uploaderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user = dataSnapshot.getValue(UserDTO.class);
                        mobile = user.getMobile();
                        email = user.getEmail();
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
                            Snackbar.make(getWindow().getDecorView().getRootView(), "सूचना सफलतापूर्वक पठाइयो", Snackbar.LENGTH_SHORT).show();

                            /*Intent intent = new Intent(Pro.this, MainDashboardActivity.class);
                            startActivity(intent);
                            finish();*/
                        }

                    }
                });

    }


    public void getProductDetails(String idOfProduct) {

        FirebaseDatabase.getInstance().getReference().child("products").child(idOfProduct)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ProductListDTO productListDTO = dataSnapshot.getValue(ProductListDTO.class);
                        try {

                            productCategory = productListDTO.getProductCategory();
                            productPrice = productListDTO.getProductPrice().toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        clientMobile = user.getMobile();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductDetailActivity.this, MainDashboardActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


    private void sendMailToUploader() {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (email != null && !email.equals("Not Available")) {
                Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", email, null));
                intent.putExtra("android.intent.extra.SUBJECT", "Smart Kishan");
                if (clientMobile != null && !clientMobile.equals("Not Available")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("I want to buy your product, Please reply to this mail or call me at ");
                    stringBuilder.append(clientMobile);
                    intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                } else {
                    intent.putExtra("android.intent.extra.TEXT", "I want to buy your product, Please reply to this mail.");
                }
                this.startActivity(intent);
            } else {
                Toast.makeText(this, "Sorry, Uploader does not provide us his/her email", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
        }
    }
}
