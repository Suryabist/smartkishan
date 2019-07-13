package com.pathibharatechnology.smartkishan.new_product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;

import java.io.IOException;

public class AddNewProductActivity extends AppCompatActivity {

    TextView selectCategoryTextView;
    ImageView imageUploadImageView;
    TextInputEditText productNameEdittext, productPriceEdittext, productDescriptionEdittext, productDeliverLocationEdittext;
    String productName, productDescription, productDeliveryLocation;
    Integer productPrice = 0;
    String category = "";
    Button uploadButton;

    final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    Bitmap bitmap;
    StorageReference storageReference;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Upload Product", true);

        selectCategoryTextView = findViewById(R.id.selectCategoryID);
        productNameEdittext = findViewById(R.id.productNameEditTextID);
        productPriceEdittext = findViewById(R.id.productPriceEdittextID);
        productDescriptionEdittext = findViewById(R.id.productDescriptionEdittextID);
        productDeliverLocationEdittext = findViewById(R.id.productDeliveryLocationEdittextID);
        uploadButton = findViewById(R.id.uploadButtonID);
        imageUploadImageView  = findViewById(R.id.imageID);
        progressBar = findViewById(R.id.progressBarID);



        imageUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });


        selectCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(AddNewProductActivity.this, selectCategoryTextView);
                popupMenu.getMenu().add(0, 0, 0, "Select a category");
                popupMenu.getMenu().add(1, 1, 1, "Fruits");
                popupMenu.getMenu().add(2,2,2,"Meats and fishes");
                popupMenu.getMenu().add(3,3,3,"Vegetables");
                popupMenu.getMenu().add(4,4,4,"Animalistic");
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem menuItem) {
                        if (menuItem.getItemId()==0 ) {
                            selectCategoryTextView.setText(menuItem.getTitle());
                            category = "";
                        } else {
                            selectCategoryTextView.setText(menuItem.getTitle());
                            category = menuItem.getTitle().toString();
                        }
                        return false;
                    }
                });
            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (category.equals("")){
                        Snackbar.make(view, "Select a category", Snackbar.LENGTH_SHORT)
                                .setAction("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .show();
                    }else {
                        addPostToDatabase();
                    }
                } else {

                }
            }
        });





    }

    private void addPostToDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        final ProductListDTO productListDTO = new ProductListDTO();
        String productId = FirebaseDatabase.getInstance().getReference().child("products").push().getKey();
        productListDTO.setProductUploaderUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        productListDTO.setProductId(productId);
        productListDTO.setProductName(productName);
        productListDTO.setProductDescription(productDescription);
        productListDTO.setProductCategory(category);
        productListDTO.setProductDeliveryLocation(productDeliveryLocation);
        productListDTO.setProductPrice(productPrice);

        if (bitmap != null) {

            storageReference = FirebaseStorage.getInstance().getReference().child("product_pictures").child(productId)
                    .child(String.valueOf(System.currentTimeMillis()));
            storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        String downloadurl = uri.toString();
                        productListDTO.setProductImageUrl(downloadurl);
                        uploadPost(productListDTO);
                    }

                }
            });
        } else {
            productListDTO.setProductImageUrl("");
            uploadPost(productListDTO);

        }
    }

    private void uploadPost(ProductListDTO post) {
        FirebaseDatabase.getInstance().getReference().child("products")
                .child(post.getProductId())
                .setValue(post)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(getWindow().getDecorView().getRootView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
                            progressBar.setVisibility(View.GONE);
//                            onBackPressed();
                            Toast.makeText(AddNewProductActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddNewProductActivity.this, MainDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });

    }


    private boolean validate() {
        boolean isValid=false;
        productName=productNameEdittext.getText().toString();
        productPrice= Integer.parseInt(productPriceEdittext.getText().toString());
        productDescription = productDescriptionEdittext.getText().toString();
        productDeliveryLocation = productDeliverLocationEdittext.getText().toString();
        if(TextUtils.isEmpty(productName)){
            productNameEdittext.setError("Required");
        }else if(productPrice==0){
            productPriceEdittext.setError("Required");
        }else if(TextUtils.isEmpty(productDescription)){
            productDescriptionEdittext.setError("Required");
        }else if(TextUtils.isEmpty(productDeliveryLocation)){
            productDeliverLocationEdittext.setError("Required");
        }else {
            isValid=true;

        }
        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageUploadImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddNewProductActivity.this, MainDashboardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();

    }
}
