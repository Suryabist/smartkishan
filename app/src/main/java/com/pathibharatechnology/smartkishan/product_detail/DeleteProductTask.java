package com.pathibharatechnology.smartkishan.product_detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class DeleteProductTask {

    boolean deleteResponse = false;
    ProductListDTO productListDTO;

    public void deleteProduct(String productId, final Context context){

        productListDTO = new ProductListDTO();

        FirebaseDatabase.getInstance().getReference().child("products")
                .child(productId)
                .setValue(productListDTO)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "उत्पादन मेटाउन सक्दैन।", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isComplete()) {
                            Toast.makeText(context, "उत्पादन सफलतापूर्वक हटाइयो।", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainDashboardActivity.class);
                            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

}
