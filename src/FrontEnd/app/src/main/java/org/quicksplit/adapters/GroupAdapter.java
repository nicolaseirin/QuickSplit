package org.quicksplit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<GroupModelIn> groups;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {

        void onReportClick(GroupModelIn group);

        void onModifyClick(GroupModelIn group);

        void onDeleteClick(GroupModelIn group);

        void onLeaveClick(GroupModelIn group);
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
    public void onBindViewHolder(@NonNull final GroupViewHolder groupViewHolder, int i) {
        final GroupModelIn currentItem = groups.get(i);
        groupViewHolder.mTextViewGroupName.setText(currentItem.getName());

        AvatarAdapter avatarAdapter = new AvatarAdapter(currentItem.getMemberships());
        groupViewHolder.mRecyclerView.setHasFixedSize(true);
        groupViewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        groupViewHolder.mRecyclerView.setAdapter(avatarAdapter);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public GroupAdapter(List<GroupModelIn> groups, Context context) {
        this.groups = groups;
        this.context = context;
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView mRecyclerView;
        public TextView mTextViewGroupName;

        public ImageView mImageViewReport;
        public ImageView mImageEdit;
        public ImageView mImageLeave;
        public ImageView mImageDelete;


        public GroupViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<GroupModelIn> groups) {
            super(itemView);

            mRecyclerView = itemView.findViewById(R.id.rview_avatars);
            mTextViewGroupName = itemView.findViewById(R.id.txt_groupName);

            mImageViewReport = itemView.findViewById(R.id.img_report);
            mImageViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onReportClick(groups.get(i));
                        }
                    }
                }
            });

            mImageEdit = itemView.findViewById(R.id.img_modify);
            mImageEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onModifyClick(groups.get(i));
                        }
                    }
                }
            });
            mImageLeave = itemView.findViewById(R.id.img_exit);
            mImageLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onLeaveClick(groups.get(i));
                        }
                    }
                }
            });

            mImageDelete = itemView.findViewById(R.id.img_delete);
            mImageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(groups.get(i));
                        }
                    }
                }
            });
        }
    }
}
