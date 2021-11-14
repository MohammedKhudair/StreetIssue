package com.barmej.streetissues.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barmej.streetissues.adapters.IssueListAdapter;
import com.barmej.streetissues.data.StreetIssueItem;
import com.barmej.streetissues.activitys.IssueDetailsActivity;
import com.barmej.streetissues.databinding.FragmentIssuesListBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY;

public class IssuesListFragment extends Fragment implements IssueListAdapter.OnStreetIssueItemClickListener {
    FragmentIssuesListBinding binding;

    private ArrayList<StreetIssueItem> mIssueItems;
    private IssueListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIssuesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mIssueItems = new ArrayList<>();
        mAdapter = new IssueListAdapter(mIssueItems, IssuesListFragment.this);
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Restoring RecyclerView scroll position
        binding.recyclerView.getAdapter().setStateRestorationPolicy(PREVENT_WHEN_EMPTY);



        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("StreetsIssues").orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    mIssueItems.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        mIssueItems.add(document.toObject(StreetIssueItem.class));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onStreetIssueClicked(StreetIssueItem streetIssueItem) {
        Intent intent = new Intent(getContext(), IssueDetailsActivity.class);
        intent.putExtra(IssueDetailsActivity.STREET_ISSUE_DATA, streetIssueItem);

        // هنا قمنا بارسال الاحداثيات لان Parcelable لايدعم تحويل ال GeoPoint
        double latitude = streetIssueItem.getLocation().getLatitude();
        double longitude = streetIssueItem.getLocation().getLongitude();
        String location = "احداثيات الموقع:\n" + "lat=" + latitude + " , lon=" + longitude;
        intent.putExtra("LOCATION", location);

        startActivity(intent);
    }

}