package com.pathibharatechnology.smartkishan.products_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pathibharatechnology.smartkishan.R;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.displayProductDetail(productListDTOS.get(position));
    }

    @Override
    public int getItemCount() {
        return productListDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextview, productPriceTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageviewID);
            productNameTextview = itemView.findViewById(R.id.productNameTextviewID);
            productPriceTextview = itemView.findViewById(R.id.productPriceTextviewID);
        }

        public void displayProductDetail(ProductListDTO productListDTO) {
            String image = productListDTO.getProductImageUrl();
            if (image.equals("")){
                Glide.with(productImageView.getContext())
                        .load(R.drawable.product_detail_bg_pic)
                        .asBitmap()
                        .into(productImageView);
            }else{
                Glide.with(productImageView.getContext())
                        .load(image)
                        .asBitmap()
                        .into(productImageView);
            }

            productNameTextview.setText(productListDTO.getProductName());
            System.out.println("Product price is===== "+productListDTO.getProductPrice());
            String priceTag = "NRs. "+ productListDTO.getProductPrice().toString();
            productPriceTextview.setText(priceTag);
        }
    }

}
