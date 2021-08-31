package com.barmej.streetissues.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.streetissues.data.StreetIssueItem;
import com.barmej.streetissues.databinding.ItemIssueBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class IssueListAdapter extends RecyclerView.Adapter<IssueListAdapter.IssueListViewHolder> {

    private List<StreetIssueItem> mStreetIssueItemsList;
    OnStreetIssueItemClickListener itemClickListener;

    public IssueListAdapter(List<StreetIssueItem> mStreetIssueItemsList, OnStreetIssueItemClickListener itemClickListener) {
        this.mStreetIssueItemsList = mStreetIssueItemsList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public IssueListAdapter.IssueListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIssueBinding binding = ItemIssueBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IssueListViewHolder(binding, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueListAdapter.IssueListViewHolder holder, int position) {
        StreetIssueItem issueItem = mStreetIssueItemsList.get(position);
        holder.bind(issueItem);

    }

    @Override
    public int getItemCount() {
        if (mStreetIssueItemsList != null) {
            return mStreetIssueItemsList.size();
        } else
            return 0;
    }

    public class IssueListViewHolder extends RecyclerView.ViewHolder {
        ItemIssueBinding binding;
        StreetIssueItem streetIssueItem;

        public IssueListViewHolder(@NonNull ItemIssueBinding binding, OnStreetIssueItemClickListener itemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onStreetIssueClicked(streetIssueItem);
                }
            });
        }

        void bind(StreetIssueItem issueItem) {
            this.streetIssueItem = issueItem;
            binding.textViewItem.setText(issueItem.getTitle());
            Glide.with(binding.imageViewItem).load(issueItem.getPhoto()).into(binding.imageViewItem);
        }
    }


    public interface OnStreetIssueItemClickListener {
        void onStreetIssueClicked(StreetIssueItem streetIssueItem);
    }

}
