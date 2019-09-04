package com.pathibharatechnology.smartkishan.job;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pathibharatechnology.smartkishan.R;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            jobTitle = itemView.findViewById(R.id.jobTitleId);
            jobDesc = itemView.findViewById(R.id.jobDescId);
            jobDeadline = itemView.findViewById(R.id.jobDeadlineId);
        }

        public void bindView(JobDTO jobDTO) {

            jobTitle.setText(jobDTO.getTitle());
            jobDesc.setText(jobDTO.getDescription());
            jobDeadline.setText("Deadline: "+jobDTO.getDeadline());

        }
    }
}
