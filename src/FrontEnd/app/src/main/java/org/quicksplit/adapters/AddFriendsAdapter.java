package org.quicksplit.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsAdapter extends RecyclerView.Adapter<AddFriendsAdapter.FriendsViewHolder> {

    private List<User> users;
    private OnItemClickListener mListener;
    private Context context;
    private TokenManager tokenManager;

    public interface OnItemClickListener {
        void onAddClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_add_friend, viewGroup, false);
        this.context = view.getContext();
        this.tokenManager = new TokenManager(context);
        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view, mListener, users);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder friendsViewHolder, int i) {
        User currentItem = users.get(i);
        /*String userId = tokenManager.getUserIdFromToken();

        Bitmap avatar;

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call call = client.getUserAvatar(userId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("Recibí la imagen");

                Bitmap avatar = BitmapFactory.decodeStream(((ResponseBody)response.body()).byteStream());
                friendsViewHolder.mImageView.setImageBitmap(avatar);

                //imageView.setImageBitmap(bmp);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                System.out.println("No recibí la imagen");
            }
        });*/

        friendsViewHolder.mTextViewNameLastname.setText(currentItem.getName() + " " + currentItem.getLastName());
        friendsViewHolder.mTextViewEmail.setText(currentItem.getMail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public AddFriendsAdapter(List<User> users) {
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
                            listener.onAddClick(users.get(i));
                        }
                    }
                }
            });
        }
    }
}