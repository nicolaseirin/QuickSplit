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
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private List<Purchase> purchases;
    private List<User> currentMembers;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {

        void onModifyClick(Purchase purchase);

        void onDeleteClick(Purchase purchase);

        void onLeaveClick(Purchase purchase);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_purchase, viewGroup, false);
        PurchaseViewHolder purchasesViewHolder = new PurchaseViewHolder(view, mListener, purchases);
        return purchasesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PurchaseViewHolder groupViewHolder, int i) {
        final Purchase currentItem = purchases.get(i);
        groupViewHolder.mTextViewPurchaseName.setText(currentItem.getName());
        groupViewHolder.mTextViewCurrency.setText(currentItem.getCurrency());

        //For sigle item call the users
        TokenManager tokenManager = new TokenManager(context);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());


        //TODO: ESTO ESTÁ MAL!! ACÁ HAY QUE PEDIR LOS MIEMBROS DE UNA COMPRA
        Call<List<User>> call = client.getGroupMembers(currentItem.getId());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    currentMembers = response.body();
                    AvatarAdapter avatarAdapter = new AvatarAdapter(currentMembers);
                    groupViewHolder.mRecyclerView.setHasFixedSize(true);
                    groupViewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    groupViewHolder.mRecyclerView.setAdapter(avatarAdapter);
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    public PurchaseAdapter(List<Purchase> purchases, Context context) {
        this.purchases = purchases;
        this.context = context;
    }

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView mRecyclerView;

        public ImageView mImageEdit;
        public ImageView mImageLeave;
        public ImageView mImageDelete;

        public TextView mTextViewPurchaseName;
        public TextView mTextViewCurrency;

        public PurchaseViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Purchase> purchases) {
            super(itemView);

            mRecyclerView = itemView.findViewById(R.id.rview_avatars);
            mTextViewPurchaseName = itemView.findViewById(R.id.txt_purchaseName);
            mTextViewCurrency = itemView.findViewById(R.id.txt_currency);
            mImageEdit = itemView.findViewById(R.id.img_modify);
            mImageEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int i = getAdapterPosition();
                        if (i != RecyclerView.NO_POSITION) {
                            listener.onModifyClick(purchases.get(i));
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
                            listener.onLeaveClick(purchases.get(i));
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
                            listener.onDeleteClick(purchases.get(i));
                        }
                    }
                }
            });
        }
    }
}