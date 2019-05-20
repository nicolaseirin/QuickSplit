package org.quicksplit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.quicksplit.model.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private List<User> users;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int i);
        void onDeleteClick(int i);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_friend, viewGroup, false);
        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view, mListener);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {
        User currentItem = users.get(i);
        friendsViewHolder.mTextViewNameLastname.setText(currentItem.getName() + " " + currentItem.getLastName());
        friendsViewHolder.mTextViewEmail.setText(currentItem.getMail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public FriendsAdapter(List<User> users) {
        this.users = users;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewNameLastname;
        public TextView mTextViewEmail;
        public ImageView mImageViewDelete;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewNameLastname = itemView.findViewById(R.id.txt_nameLastname);
            mTextViewEmail = itemView.findViewById(R.id.txt_email);
            mImageViewDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onItemClick(i);
                        }
                    }
                }
            });

            mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(i);
                        }
                    }
                }
            });
        }
    }
}
