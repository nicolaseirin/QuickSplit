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
        void onDeleteClick(Group group);
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(view, mListener, groups);
        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder groupViewHolder, int i) {
        Group currentItem = groups.get(i);

        groupViewHolder.mTextViewGroupName.setText(currentItem.getName());
        //TODO: PASAR LOS ID DE LOS MIEMBROS A LOS MIEMBROS REALES
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageViewGroup;
        public TextView mTextViewGroupName;
        public TextView mTextViewMembers;

        public GroupViewHolder(@NonNull View itemView, final GroupAdapter.OnItemClickListener listener, final List<Group> groups) {
            super(itemView);

            mImageViewGroup = itemView.findViewById(R.id.img_group);
            mTextViewGroupName = itemView.findViewById(R.id.txt_groupName);
            mTextViewMembers = itemView.findViewById(R.id.txt_members);
        }
    }
}
