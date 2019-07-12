package com.pathibharatechnology.smartkishan.products_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.product_detail.ProductDetailActivity;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    List<ProductListDTO> productListDTOS;
    Context mContext;

    public ProductListAdapter(List<ProductListDTO> productListDTOS, Context mContext) {
        this.productListDTOS = productListDTOS;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.displayProductDetail(productListDTOS.get(position));
    }

    @Override
    public int getItemCount() {
        return productListDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextview, productPriceTextview;
        LinearLayout recyclerLinearLayout;
        String imageUrl, productName, productDetail, deliveryLocation;
        Integer price;
        String userID, uploaderUserName, uploaderImageUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageviewID);
            productNameTextview = itemView.findViewById(R.id.productNameTextviewID);
            productPriceTextview = itemView.findViewById(R.id.productPriceTextviewID);
            recyclerLinearLayout = itemView.findViewById(R.id.recyclerLinearLayoutID);
        }

        public void displayProductDetail(final ProductListDTO productListDTO) {

            imageUrl = productListDTO.getProductImageUrl();
            productName = productListDTO.getProductName();
            productDetail = productListDTO.getProductDescription();
            System.out.println("Product detail from firebase===="+productDetail);
            userID = productListDTO.getProductUploaderUserId();
            deliveryLocation = productListDTO.getProductDeliveryLocation();
            price = productListDTO.getProductPrice();

            if (imageUrl.equals("")){
                Glide.with(productImageView.getContext())
                        .load(R.drawable.product_detail_bg_pic)
                        .asBitmap()
                        .into(productImageView);
            }else{
                Glide.with(productImageView.getContext())
                        .load(imageUrl)
                        .asBitmap()
                        .into(productImageView);
            }

            productNameTextview.setText(productListDTO.getProductName());
            System.out.println("Product price is===== "+productListDTO.getProductPrice());
            final String priceTag = "NRs. "+ productListDTO.getProductPrice().toString();
            productPriceTextview.setText(priceTag);

            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    uploaderUserName = (String) dataSnapshot.child("userName").getValue();
                    uploaderImageUrl = (String) dataSnapshot.child("profilePic").getValue();
                    System.out.println("Username from firebase is====="+uploaderUserName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("error occured");
                    System.out.println("====="+databaseError.getMessage());
                }
            });


            recyclerLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProductDetailActivity.class);
                    intent.putExtra("userId", userID);
                    intent.putExtra("uploaderUserName", uploaderUserName);
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("productName", productName);
                    intent.putExtra("productPrice", price);
                    intent.putExtra("productDetail", productDetail);
                    intent.putExtra("deliveryLocation", deliveryLocation);
                    intent.putExtra("uploaderImageUrl", uploaderImageUrl);
                    mContext.startActivity(intent);

                }
            });
        }
    }

}
