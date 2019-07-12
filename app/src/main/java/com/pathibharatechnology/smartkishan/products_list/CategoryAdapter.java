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
import com.pathibharatechnology.smartkishan.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    ArrayList<String> categoryList = new ArrayList<>();
    HashMap<String, String> categoryDataList = new HashMap<>();
    Context mContext;

    public CategoryAdapter(ArrayList<String> categoryList, HashMap<String, String> categoryDataList, Context mContext) {
        this.categoryList = categoryList;
        this.categoryDataList = categoryDataList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_container, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setCategoryList(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout categoryLinearLayout;
        ImageView categoryImageview;
        TextView categoryTextview;
        String categoryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryLinearLayout = itemView.findViewById(R.id.categoryLayoutID);
            categoryImageview = itemView.findViewById(R.id.categoryImageID);
            categoryTextview = itemView.findViewById(R.id.categoryTextID);
        }

        public void setCategoryList(String s) {
            categoryText = categoryDataList.get(s);
            int id = mContext.getResources().getIdentifier("com.pathibharatechnology.smartkishan:drawable/" + s, null, null);
            categoryImageview.setImageResource(id);
            categoryTextview.setText(categoryText);


            categoryLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CategoryProductList.class);
                    intent.putExtra("category", categoryText);
                    mContext.startActivity(intent);
                }
            });

        }
    }

}
