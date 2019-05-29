package org.quicksplit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.quicksplit.R;
import org.quicksplit.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groups;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAddClick(Group user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        GroupViewHolder friendsViewHolder = new GroupViewHolder(view, mListener, groups);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {
        Group currentItem = groups.get(i);
        groupViewHolder.mTextViewGroupName.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public GroupAdapter(List<Group> groups) {
        this.groups = groups;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageViewGroup;
        public TextView mTextViewGroupName;
        public TextView mTextViewMembers;

        public GroupViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Group> users) {
            super(itemView);
            mTextViewGroupName = itemView.findViewById(R.id.txt_groupName);

        }
    }
}
