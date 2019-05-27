package org.quicksplit.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.quicksplit.R;
import org.quicksplit.Utils;
import org.quicksplit.models.User;

import java.util.List;

public class GroupFriendsAdapter extends RecyclerView.Adapter<GroupFriendsAdapter.FriendsViewHolder> {

    private List<User> users;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onAddClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group_friends, viewGroup, false);
        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view, mListener, users);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {
        User currentItem = users.get(i);

        String avatar = currentItem.getAvatar();
        friendsViewHolder.mImageView.setImageBitmap(Utils.stringToBitMap(avatar));
        friendsViewHolder.mTextViewNameLastname.setText(currentItem.getName() + " " + currentItem.getLastName());
        friendsViewHolder.mTextViewEmail.setText(currentItem.getMail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public GroupFriendsAdapter(List<User> users) {
        this.users = users;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewNameLastname;
        public TextView mTextViewEmail;
        public CheckBox mCheckboxFriend;

        public FriendsViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<User> users) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextViewNameLastname = itemView.findViewById(R.id.txt_nameLastname);
            mTextViewEmail = itemView.findViewById(R.id.txt_email);
            mCheckboxFriend =  itemView.findViewById(R.id.cbox_addToGroup);

            //TODO: VER COMO SACAR DE LA RECYCLER VIEW LOS QUE ESTÁN SELECCIONADOS

            /*
            mCheckboxFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onAddClick(users.get(i));
                        }
                    }
                }
            });*/
        }
    }
}