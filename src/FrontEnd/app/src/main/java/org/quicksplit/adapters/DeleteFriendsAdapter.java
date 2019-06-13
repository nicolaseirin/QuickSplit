package org.quicksplit.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.Utils;
import org.quicksplit.models.User;

import java.util.List;

public class DeleteFriendsAdapter extends RecyclerView.Adapter<DeleteFriendsAdapter.FriendsViewHolder> {

    private List<User> users;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onDeleteClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_delete_friend, viewGroup, false);
        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view, mListener, users);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {
        User currentItem = users.get(i);


        Uri imageUri = Uri.parse(ServiceGenerator.getBaseUrl() + currentItem.getAvatar());
        Picasso.get()
                .load(imageUri)
                .resize(100, 100)
                .centerCrop()
                .into(friendsViewHolder.mImageView);

        friendsViewHolder.mTextViewNameLastname.setText(currentItem.toString());
        friendsViewHolder.mTextViewEmail.setText(currentItem.getMail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public DeleteFriendsAdapter(List<User> users) {
        this.users = users;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewNameLastname;
        public TextView mTextViewEmail;
        public ImageView mImageViewDelete;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<User> users) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewNameLastname = itemView.findViewById(R.id.txt_nameLastname);
            mTextViewEmail = itemView.findViewById(R.id.txt_email);
            mImageViewDelete = itemView.findViewById(R.id.img_delete);

            mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(users.get(i));
                        }
                    }
                }
            });
        }
    }
}
