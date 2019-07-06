package com.pathibharatechnology.smartkishan.new_product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.products_list.ListProductsFragment;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class AddNewProductFragment extends Fragment {


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_product, container, false);
        selectCategoryTextView = view.findViewById(R.id.selectCategoryID);
        productNameEdittext = view.findViewById(R.id.productNameEditTextID);
        productPriceEdittext = view.findViewById(R.id.productPriceEdittextID);
        productDescriptionEdittext = view.findViewById(R.id.productDescriptionEdittextID);
        productDeliverLocationEdittext = view.findViewById(R.id.productDeliveryLocationEdittextID);
        uploadButton = view.findViewById(R.id.uploadButtonID);
        imageUploadImageView  = view.findViewById(R.id.imageID);
        progressBar = view.findViewById(R.id.progressBarID);


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
                final PopupMenu popupMenu = new PopupMenu(getContext(), selectCategoryTextView);
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
                        Snackbar.make(getView(), "Select a category", Snackbar.LENGTH_SHORT)
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

        return view;
    }


    private void addPostToDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        final ProductListDTO post = new ProductListDTO();
        String postid = FirebaseDatabase.getInstance().getReference().child("products").push().getKey();
        post.setProductUploaderUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setProductId(postid);
        post.setProductName(productName);
        post.setProductDescription(productDescription);
        post.setProductCategory(category);
        post.setProductDeliveryLocation(productDeliveryLocation);
        post.setProductPrice(productPrice);

        if (bitmap != null) {

            storageReference = FirebaseStorage.getInstance().getReference().child("post_pictures").child(postid)
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
                        post.setProductImageUrl(downloadurl);
                        uploadPost(post);
                    }

                }
            });
        } else {
            post.setProductImageUrl("");
            uploadPost(post);

        }
    }


    private void uploadPost(ProductListDTO post) {
        FirebaseDatabase.getInstance().getReference().child("posts")
                .child(post.getProductId())
                .setValue(post)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
                            progressBar.setVisibility(View.GONE);
//                            onBackPressed();
                            Snackbar.make(getView(), "Successfully uploaded...", Snackbar.LENGTH_SHORT).show();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                                    .replace(R.id.dashboardFrameID, new ListProductsFragment());
                            fragmentTransaction.commit();
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
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                imageUploadImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



}
