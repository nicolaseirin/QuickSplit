package org.quicksplit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolderUser>{

    ArrayList<String> users;

    public AdapterUser(ArrayList<String> users){
        this.users = users;
    }
    @NonNull
    @Override
    public AdapterUser.ViewHolderUser onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_list,null, false);
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUser.ViewHolderUser viewHolderUser, int i) {
        viewHolderUser.assignData(users.get(i));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder {

        TextView user;
        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.UserId);
        }

        public void assignData(String s) {
            user.setText(s);
        }
    }
}