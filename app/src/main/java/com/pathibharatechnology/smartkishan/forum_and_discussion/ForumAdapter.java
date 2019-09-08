package com.pathibharatechnology.smartkishan.forum_and_discussion;

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

import java.util.ArrayList;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    ArrayList<String> names;
    Context mContext;

    public ForumAdapter(ArrayList<String> arraylist, Context mContext) {
        this.names = arraylist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_display, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.displayContent(names.get(i));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, date, content, like, liked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageOfPostUploaderID);
            name = itemView.findViewById(R.id.postUploaderNameID);
            date = itemView.findViewById(R.id.postUploadTimeID);
            content = itemView.findViewById(R.id.contentID);
            like = itemView.findViewById(R.id.likeID);
            liked = itemView.findViewById(R.id.likedID);
        }

        public void displayContent(String s) {

            Glide.with(mContext)
                    .load(R.drawable.luffy)
                    .asBitmap()
                    .into(image);

            name.setText(s);
            date.setText("2019-06-09");
            content.setText(R.string.dummy_data);

            /*final boolean[] isChecked = {false};

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked[0] == false) {
                        like.setVisibility(View.GONE);
                        liked.setVisibility(View.VISIBLE);
                        isChecked[0] = true;
                    } else {
                        liked.setVisibility(View.GONE);
                        like.setVisibility(View.VISIBLE);
                        isChecked[0] = false;
                    }
                }
            });

            liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChecked[0] == true) {
                        like.setVisibility(View.GONE);
                        liked.setVisibility(View.VISIBLE);
                        isChecked[0] = false;
                    } else {
                        liked.setVisibility(View.GONE);
                        like.setVisibility(View.VISIBLE);
                        isChecked[0] = true;
                    }
                }
            });*/

        }
    }

}
