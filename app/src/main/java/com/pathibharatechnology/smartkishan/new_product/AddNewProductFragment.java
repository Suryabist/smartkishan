package com.pathibharatechnology.smartkishan.new_product;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.pathibharatechnology.smartkishan.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class AddNewProductFragment extends Fragment {


    TextView selectCategoryTextView;
    ImageView imageUploadImageView;
    TextInputEditText productNameEdittext, productPriceEdittext, productDescriptionEdittext, productDeliverLocationEdittext;
    String productName, productPrice, productDescription, productDeliveryLocation;
    String category = "";
    Button uploadButton;

    final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    Bitmap bitmap;
    StorageReference storageReference;

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
                        Toast.makeText(getContext(), "Verified successfully.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        });

        return view;
    }

    private boolean validate() {
        boolean isValid=false;
        productName=productNameEdittext.getText().toString();
        productPrice=productPriceEdittext.getText().toString();
        productDescription = productDescriptionEdittext.getText().toString();
        productDeliveryLocation = productDeliverLocationEdittext.getText().toString();
        if(TextUtils.isEmpty(productName)){
            productNameEdittext.setError("Required");
        }else if(TextUtils.isEmpty(productPrice)){
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
