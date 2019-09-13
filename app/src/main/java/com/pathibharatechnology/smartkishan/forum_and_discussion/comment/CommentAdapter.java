package com.pathibharatechnology.smartkishan.forum_and_discussion.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<CommentDTO> commentList;

    public CommentAdapter(List<CommentDTO> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindView(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView showComment;
        TextView uploaderName;
        CircleImageView commenterImage;

        public ViewHolder(View itemView) {
            super(itemView);
            showComment=itemView.findViewById(R.id.show_comment);
            commenterImage = itemView.findViewById(R.id.commenterImageId);
            uploaderName = itemView.findViewById(R.id.uploaderNameId);
        }
        public void bindView(final CommentDTO comment){
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(comment.getCommeneterId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserDTO user = dataSnapshot.getValue(UserDTO.class);
                            Glide.with(commenterImage.getContext())
                                    .load(user.getProfilePic())
                                    .asBitmap()
                                    .into(commenterImage);
                            uploaderName.setText(user.getFullName());
                            showComment.setText(comment.getComment());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }
    }
}
