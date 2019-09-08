package com.pathibharatechnology.smartkishan.job;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    List<JobDTO> jobDTOList;
    Context context;

    public JobAdapter(List<JobDTO> jobDTOList, Context context) {
        this.jobDTOList = jobDTOList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(jobDTOList.get(position));
    }

    @Override
    public int getItemCount() {
        return jobDTOList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView jobTitle, jobDesc, jobDeadline;
        LinearLayout jobListLayout;
        String mobile;
        String company, title, location, expiryDate, postedDate, description, postedBy, uploaderName, uploaderProfilePic;
        Long salary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.jobTitleId);
            jobDesc = itemView.findViewById(R.id.jobDescId);
            jobDeadline = itemView.findViewById(R.id.jobDeadlineId);
            jobListLayout = itemView.findViewById(R.id.jobListLayoutId);
        }

        public void bindView(final JobDTO jobDTO) {

            company = jobDTO.getCompany();
            title = jobDTO.getTitle();
            location = jobDTO.getLocation();
            expiryDate = jobDTO.getDeadline();
            description = jobDTO.getDescription();
            postedBy = jobDTO.getUploaderId();
            postedDate = jobDTO.getPostedDate();
            salary = jobDTO.getSalary();

            getJobUploaderDetail(jobDTO.getUploaderId());

            jobTitle.setText(title);
            jobDesc.setText(description);
            jobDeadline.setText("Deadline: "+jobDTO.getDeadline());
            jobListLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("company", company);
                    intent.putExtra("title", title);
                    intent.putExtra("location", location);
                    intent.putExtra("expiryDate", expiryDate);
                    intent.putExtra("description", description);
                    intent.putExtra("postedById", postedBy);
                    intent.putExtra("salary", salary);
                    intent.putExtra("uploaderName", uploaderName);
                    intent.putExtra("uploaderProfilePic", uploaderProfilePic);
                    intent.putExtra("postedDate", postedDate);
                    intent.putExtra("mobile", mobile);
                    context.startActivity(intent);
                }
            });
        }

        public void getJobUploaderDetail(final String uploaderId){

            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(uploaderId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserDTO user = dataSnapshot.getValue(UserDTO.class);
                            uploaderName = user.getUserName();
                            uploaderProfilePic = user.getProfilePic();
                            mobile = user.getMobile();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
    }
}
