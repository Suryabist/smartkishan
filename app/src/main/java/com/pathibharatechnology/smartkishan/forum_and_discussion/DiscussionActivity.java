package com.pathibharatechnology.smartkishan.forum_and_discussion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.pathibharatechnology.smartkishan.R;

import java.util.ArrayList;

public class DiscussionActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);


        recyclerView = findViewById(R.id.recyclerViewID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        names = new ArrayList<>();

        names.add("Surya Ghising");
        names.add("Arijit singh");
        names.add("Steve Bhai");
        names.add("Bittu Bacchan");


        ForumAdapter adapter = new ForumAdapter(names, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
