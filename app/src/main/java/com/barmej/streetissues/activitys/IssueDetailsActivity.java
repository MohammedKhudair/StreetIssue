package com.barmej.streetissues.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.barmej.streetissues.data.StreetIssueItem;
import com.barmej.streetissues.databinding.ActivityIssueDetailsBinding;
import com.bumptech.glide.Glide;

public class IssueDetailsActivity extends AppCompatActivity {
    private ActivityIssueDetailsBinding binding;
    public static final String STREET_ISSUE_DATA = "StreetIssue_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssueDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent() != null && getIntent().getExtras() != null) {
            StreetIssueItem streetIssueItem = getIntent().getExtras().getParcelable(STREET_ISSUE_DATA);
            if (streetIssueItem != null) {
                getSupportActionBar().setTitle(streetIssueItem.getTitle());
                binding.textViewDescriptionDetails.setText(streetIssueItem.getDescription());
                binding.textViewLocationDetails.setText(getIntent().getStringExtra("LOCATION"));
                Glide.with(this).load(streetIssueItem.getPhoto()).into(binding.imageViewPhotoDetails);
            }
        }


    }
}