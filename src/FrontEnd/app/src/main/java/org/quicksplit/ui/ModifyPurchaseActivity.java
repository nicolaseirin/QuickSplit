package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.AddFriendsAdapter;
import org.quicksplit.adapters.DeleteFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.User;
import org.quicksplit.service.CurrencyClient;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.PurchaseClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyPurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private Purchase purchase;
    private Group group;

    private Toolbar mToolbar;

    private TextView mTextViewGroupName;

    private TextInputLayout mTextInputLayoutPurchaseName;
    private EditText mEditTextPurchaseName;

    private Spinner mSpinnerCurrency;

    private TextInputLayout mTextInputLayoutTxtCost;
    private EditText mEditTextCost;

    private List<String> currencies;
    private List<User> members;
    private List<User> participants;

    private RecyclerView.LayoutManager mRecyclerViewManager;
    private Button mButtonModifyPurchase;

    private TextInputLayout mTextInputLayoutGroupMembers;
    private RecyclerView mRecyclerViewMembers;
    private AddFriendsAdapter mRecycleViewGroupMembersAdapter;

    private RecyclerView mRecyclerViewPaticipants;
    private DeleteFriendsAdapter mRecycleViewDeleteMembersAdapter;

    private ArrayAdapter<String> currenciesArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_purchase);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerViewMembers = findViewById(R.id.membersReciclerView);
        mRecyclerViewPaticipants = findViewById(R.id.purchasersReciclerView);

        mTextViewGroupName = findViewById(R.id.txt_groupName);

        mTextInputLayoutPurchaseName = findViewById(R.id.lblError_purchaseName);
        mEditTextPurchaseName = findViewById(R.id.txt_purchaseName);

        mSpinnerCurrency = findViewById(R.id.spn_currency);

        mTextInputLayoutTxtCost = findViewById(R.id.lblError_txtCost);
        mEditTextCost = findViewById(R.id.txt_cost);

        mButtonModifyPurchase = findViewById(R.id.btn_modifyPurchase);
        mButtonModifyPurchase.setOnClickListener(this);

        mTextInputLayoutGroupMembers = findViewById(R.id.lblError_groupMembers);
        loadPurchaseData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPurchaseData() {
        String purchaseId = getIntent().getStringExtra("EXTRA_PURCHASE_ID");
        getPurchaseFromId(purchaseId);
    }

    private void getPurchaseFromId(String purchaseId) {
        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<Purchase> call = client.getPurchases(purchaseId);

        call.enqueue(new Callback<Purchase>() {
            @Override
            public void onResponse(Call<Purchase> call, Response<Purchase> response) {
                if (response.isSuccessful()) {
                    purchase = response.body();
                    getPurchaseValues();
                } else {
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al obtener compras.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Purchase> call, Throwable t) {
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la comunicación al obtener compras.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPurchaseValues() {

        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        Call<Group> call = client.getGroup(purchase.getGroup());
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    group = response.body();
                    setPurchaseValues();
                    getAvilableCurrencies();
                    getGroupMembers();
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void setPurchaseValues() {
        mTextViewGroupName.setText(group.getName());
        mEditTextPurchaseName.setText(purchase.getName());
        mEditTextCost.setText(purchase.getCost());
    }

    private void getAvilableCurrencies() {
        TokenManager tokenManager = new TokenManager(this);
        CurrencyClient client = ServiceGenerator.createService(CurrencyClient.class, tokenManager.getToken());
        Call<List<String>> call = client.getCurrencies();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    currencies = response.body();
                    buildCurrenciesAdapter();
                } else {
                    System.out.println("Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void buildCurrenciesAdapter() {

        String currency = purchase.getCurrency();
        currenciesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        currenciesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCurrency.setAdapter(currenciesArrayAdapter);

        int currencySpinnerPosition = currenciesArrayAdapter.getPosition(currency);
        mSpinnerCurrency.setSelection(currencySpinnerPosition);
    }

    private void getGroupMembers() {

        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());

        Call<List<User>> call = client.getGroupMembers(purchase.getGroup());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    members = response.body();
                    getParticipants();
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

    private void getParticipants() {
        String purchaseId = getIntent().getStringExtra("EXTRA_PURCHASE_ID");
        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getParticipants(purchaseId);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    participants = response.body();
                    buildRecyclerViewAddMembersAdapter();
                } else {
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al obtener los participantes de la compra.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la comunicación al obtener los participantes de la compra.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewAddMembersAdapter() {
        members.removeAll(participants);

        mRecyclerViewMembers.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupMembersAdapter = new AddFriendsAdapter(members);
        mRecyclerViewMembers.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewMembers.setAdapter(mRecycleViewGroupMembersAdapter);
        mRecycleViewGroupMembersAdapter.setOnItemClickListener(new AddFriendsAdapter.OnItemClickListener() {
            @Override
            public void onAddClick(User user) {
                participants.add(user);
                members.remove(user);

                buildRecyclerViewAddMembersAdapter();
                buildRecyclerViewDeleteMembersAdapter();
            }
        });

        buildRecyclerViewDeleteMembersAdapter();
    }

    private void buildRecyclerViewDeleteMembersAdapter() {
        mRecyclerViewPaticipants.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewDeleteMembersAdapter = new DeleteFriendsAdapter(participants);
        mRecyclerViewPaticipants.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewPaticipants.setAdapter(mRecycleViewDeleteMembersAdapter);
        mRecycleViewDeleteMembersAdapter.setOnItemClickListener(new DeleteFriendsAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(User user) {
                participants.remove(user);
                members.add(user);

                buildRecyclerViewDeleteMembersAdapter();
                buildRecyclerViewAddMembersAdapter();
            }
        });
    }

    @Override
    public void onClick(View v) {
        modifyPurchase();
    }

    private void modifyPurchase() {
        showErrors();

        if (!validFields())
            return;

        Purchase purchase = new Purchase();

        purchase.setName(mEditTextPurchaseName.getText().toString());
        purchase.setCurrency(mSpinnerCurrency.getSelectedItem().toString());
        purchase.setCost(mEditTextCost.getText().toString());

        ArrayList<String> participantsString = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++)
            participantsString.add(participants.get(i).getId());
        purchase.setParticipants(participantsString);

        TokenManager tokenManager = new TokenManager(this);

        PurchaseClient client = ServiceGenerator.createService(PurchaseClient.class, tokenManager.getToken());
        Call<Purchase> call = client.modifyPurchase(getIntent().getStringExtra("EXTRA_PURCHASE_ID"), purchase);

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<Purchase>() {

            @Override
            public void onResponse(Call<Purchase> call, Response<Purchase> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyPurchaseActivity.this, "Error al modificar la compra", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Purchase> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ModifyPurchaseActivity.this, "Error en la comunicación al modificar la compra.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showErrors() {
        if (!(mEditTextPurchaseName.getText().toString().trim().length() > 0))
            mTextInputLayoutPurchaseName.setError("El nombre es requerído.");
        else
            mTextInputLayoutPurchaseName.setError("");

        if (!(mEditTextCost.getText().toString().trim().length() > 0))
            mTextInputLayoutTxtCost.setError("El costo es requerído.");
        else
            mTextInputLayoutTxtCost.setError("");

        if (!(participants.size() > 0))
            mTextInputLayoutGroupMembers.setError("Por lo menos debe agregar un miembro para la compra.");
        else
            mTextInputLayoutGroupMembers.setError("");
    }

    private boolean validFields() {
        return mEditTextPurchaseName.getText().toString().trim().length() > 0
                && mEditTextCost.getText().toString().length() > 0
                && participants.size() > 0;
    }
}

