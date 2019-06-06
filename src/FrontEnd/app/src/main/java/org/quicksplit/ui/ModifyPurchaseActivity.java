package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;
import org.quicksplit.service.PurchaseClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyPurchaseActivity extends AppCompatActivity {

    private Purchase purchase;

    private TextView mTextViewGroupName;
    private EditText mEditTextPurchaseName;
    private Spinner mSpinnerCurrency;
    private EditText mEditTextCost;
    private RecyclerView mRecyclerViewPurchasers;

    private List<String> currencies;

    private List<User> members;
    private List<User> participants;


    private TextInputLayout mTextInputLayoutGroupMembers;
    private RecyclerView mRecyclerViewGroupMembers;
    private GroupFriendsAdapter mRecycleViewGroupFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_purchase);

        mTextViewGroupName = findViewById(R.id.txt_groupName);
        mEditTextPurchaseName = findViewById(R.id.txt_purchaseName);
        mSpinnerCurrency = findViewById(R.id.spn_currency);
        mEditTextCost = findViewById(R.id.txt_cost);
        mRecyclerViewPurchasers = findViewById(R.id.purchasesReciclerView);

        getPurchase();
    }

    private void getPurchase() {
        String purchaseId = getIntent().getStringExtra("EXTRA_PURCHASE_ID");
        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<Purchase> call = client.getPurchases(purchaseId);

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<Purchase>() {
            @Override
            public void onResponse(Call<Purchase> call, Response<Purchase> response) {
                if (response.isSuccessful()) {
                    purchase = response.body();
                    getPurchaseValues();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Purchase> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la comunicación al obtener grupos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPurchaseValues() {
        mTextViewGroupName.setText(purchase.getGroup());
        mEditTextPurchaseName.setText(purchase.getName());
        mEditTextCost.setText(purchase.getCurrency());
    }

    private void buildRecyclerViewCreateGroupFriendsAdapter() {
        mRecyclerViewGroupMembers.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupFriendsAdapter = new GroupFriendsAdapter(members);
        mRecyclerViewGroupMembers.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewGroupMembers.setAdapter(mRecycleViewGroupFriendsAdapter);
        mRecycleViewGroupFriendsAdapter.setCheckAllItems(true);
        mRecycleViewGroupFriendsAdapter.setOnItemClickListener(new GroupFriendsAdapter.OnItemCheckedListener() {
            @Override
            public void onCheck(User user) {
                participants.add(user);
            }

            @Override
            public void onUncheck(User user) {
                participants.remove(user);
            }
        });
    }
}

