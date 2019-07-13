package com.pathibharatechnology.smartkishan.new_product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class AddNewProductActivity extends AppCompatActivity {

    TextView selectCategoryTextView;
    ImageView imageUploadImageView;
    TextInputEditText productNameEdittext, productPriceEdittext, productDescriptionEdittext, productDeliverLocationEdittext;
    String productName, productDescription, productDeliveryLocation;
    Integer productPrice = 0;
    String category = "";
    Button uploadButton;

    Uri uri;
    Bitmap bitmap;
    StorageReference storageReference;
    String productPriceInString;
    ProgressBar progressBar;
    String buttonText = "Upload";
    String idOfProduct;

    String downloadurl = "";

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
        imageUploadImageView = findViewById(R.id.imageID);
        progressBar = findViewById(R.id.progressBarID);


        Intent intent = getIntent();

        System.out.println("intent=====" + intent);

        downloadurl = intent.getStringExtra("productImageUrl");
        productName = intent.getStringExtra("productName");
        category = intent.getStringExtra("productCategory");
        productPriceInString = intent.getStringExtra("productPrice");
        productDescription = intent.getStringExtra("productDetail");
        productDeliveryLocation = intent.getStringExtra("productDeliveryLocation");
        idOfProduct = intent.getStringExtra("productId");


        if (downloadurl == null) {

        } else {
            Glide.with(this)
                    .load(downloadurl)
                    .asBitmap()
                    .into(imageUploadImageView);
            productNameEdittext.setText(productName);

            selectCategoryTextView.setText(category);
            productPriceEdittext.setText(productPriceInString);
            productDescriptionEdittext.setText(productDescription);
            productDeliverLocationEdittext.setText(productDeliveryLocation);

            uploadButton.setText("Edit and Upload");
        }


        imageUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        selectCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(AddNewProductActivity.this, selectCategoryTextView);
                popupMenu.getMenu().add(0, 0, 0, "Select a category");
                popupMenu.getMenu().add(1, 1, 1, "Fruits");
                popupMenu.getMenu().add(2, 2, 2, "Meats and fishes");
                popupMenu.getMenu().add(3, 3, 3, "Vegetables");
                popupMenu.getMenu().add(4, 4, 4, "Animalistic");
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem menuItem) {
                        if (menuItem.getItemId() == 0) {
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
                    if (category.equals("")) {
                        Snackbar.make(view, "Select a category", Snackbar.LENGTH_SHORT)
                                .setAction("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                })
                                .show();
                    } else {


                        if (uploadButton.getText().toString().equals("Edit and Upload")) {

                            if (downloadurl.equals("")) {
                                Snackbar.make(view, "Please upload an image.", Snackbar.LENGTH_SHORT).show();
                            } else {
//                                addPostToDatabase();
                                updatePost();
                                Toast.makeText(AddNewProductActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            if (bitmap == null) {
                                Snackbar.make(view, "Please upload an image.", Snackbar.LENGTH_SHORT).show();
                            } else {

                                addPostToDatabase();
                            }
                        }

                    }
                } else {

                }
            }
        });
    }


    private void updatePost(){
        progressBar.setVisibility(View.VISIBLE);
        final ProductListDTO productListDTO = new ProductListDTO();
        productListDTO.setProductUploaderUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        productListDTO.setProductId(idOfProduct);
        productListDTO.setProductName(productName);
        productListDTO.setProductDescription(productDescription);
        productListDTO.setProductCategory(category);
        productListDTO.setProductDeliveryLocation(productDeliveryLocation);
        productListDTO.setProductPrice(productPrice);

        if (bitmap != null) {

            storageReference = FirebaseStorage.getInstance().getReference().child("product_pictures").child(idOfProduct)
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
                        downloadurl = uri.toString();
                        productListDTO.setProductImageUrl(downloadurl);
                        uploadPost(productListDTO);
                    }

                }
            });
        } else {
            productListDTO.setProductImageUrl(downloadurl);
            uploadPost(productListDTO);
        }


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
                        downloadurl = uri.toString();
                        productListDTO.setProductImageUrl(downloadurl);
                        uploadPost(productListDTO);
                    }

                }
            });
        }
    }

    private void uploadPost(ProductListDTO post) {

        System.out.println("post data ====="+post.getProductId());


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
        boolean isValid = false;
        productName = productNameEdittext.getText().toString();
        productDescription = productDescriptionEdittext.getText().toString();
        productDeliveryLocation = productDeliverLocationEdittext.getText().toString();
        if (TextUtils.isEmpty(productName)) {
            productNameEdittext.setError("Required");
        } else if (productPriceEdittext.getText().toString().equals("")) {
            productPriceEdittext.setError("Required");
        } else if (TextUtils.isEmpty(productDescription)) {
            productDescriptionEdittext.setError("Required");
        } else if (TextUtils.isEmpty(productDeliveryLocation)) {
            productDeliverLocationEdittext.setError("Required");
        } else {
            productPrice = Integer.parseInt(productPriceEdittext.getText().toString());
            isValid = true;

        }
        return isValid;
    }


    private void selectImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    private void cropRequest() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                cropRequest();
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uri = resultUri;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageUploadImageView.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
