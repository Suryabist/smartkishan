package com.pathibharatechnology.smartkishan.notification_package;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.pathibharatechnology.smartkishan.user_profile.UserDetailActivity;
import com.pathibharatechnology.smartkishan.user_profile.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<NotificationDTO> notificationDTOList;
    List<NotificationDTO> fullNotificationDtoList;
    Context mContext;

    String userProfilePic;

    public NotificationAdapter(List<NotificationDTO> notificationDTOList, Context mContext) {
        this.notificationDTOList = notificationDTOList;
        fullNotificationDtoList = new ArrayList<>(notificationDTOList);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notification, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<NotificationDTO> notificationReverse = new ArrayList<>();
        for (int i=(notificationDTOList.size()-1); i>=0; i--){
            notificationReverse.add(notificationDTOList.get(i));
        }
        holder.setNotificationListIntoHolder(notificationReverse.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationDTOList.size();
    }

    public int performFiltering() {
        List<NotificationDTO> filteredList = new ArrayList<>();

        for (NotificationDTO notificationDTO : fullNotificationDtoList) {
            if (notificationDTO.getStatusRead().toString().equals("false")) {
                filteredList.add(notificationDTO);
            }
        }
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTextview, notificationDateTextview;
        RelativeLayout notificationLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTextview = itemView.findViewById(R.id.notificationId);
            notificationDateTextview = itemView.findViewById(R.id.notificationDateId);
            notificationLayout = itemView.findViewById(R.id.notificationLayoutId);
        }

        public void setNotificationListIntoHolder(final NotificationDTO notificationDTO) {

            notificationTextview.setText(notificationDTO.getNotificationSenderName() + " wants to buy your product " + notificationDTO.getProductName() + ". Click to visit his profile.");
            notificationDateTextview.setText(notificationDTO.getCreatedDate());

            if (notificationDTO.getStatusRead().toString().equals("false")){
                notificationTextview.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                notificationTextview.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }

            getUserDetails();

            notificationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateStatusRead(notificationDTO.notificationId);
                    Intent intent = new Intent(mContext, UserDetailActivity.class);
                    intent.putExtra("userName", notificationDTO.notificationSenderName);
                    intent.putExtra("userId", notificationDTO.notificationSenderId);
                    intent.putExtra("userProfilePic", userProfilePic);
                    mContext.startActivity(intent);




                }
            });


        }

        public void updateStatusRead(String notificationId){

            FirebaseDatabase.getInstance().getReference().child("notifications").child(notificationId).child("statusRead")
                    .setValue(true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Success.");
                        }
                    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
            ;
        }

    }



    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);
                        userProfilePic = user.getProfilePic();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
