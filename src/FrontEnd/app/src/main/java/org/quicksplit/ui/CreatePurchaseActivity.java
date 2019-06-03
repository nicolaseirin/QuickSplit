package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.Purchase;
import org.quicksplit.models.Token;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePurchaseActivity extends AppCompatActivity implements  View.OnClickListener {

    private List<Group> groups;
    private ArrayAdapter<Group> groupArrayAdapter;

    private List<String> currency;

    private List<User> members;
    private List<User> participants;

    private TextInputLayout mTextInputLayotSpnGroupName;
    private Spinner mSpinnerGroups;
    private TextInputLayout mTextInputLayotSpnCurrency;
    private Spinner mSpinnerCurrency;
    private TextInputLayout mTextInputLayotTxtCost;
    private EditText mEditTextCost;
    private RecyclerView mRecyclerViewGroupMembers;
    private GroupFriendsAdapter mRecycleViewGroupFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;
    private Button mButtonCreatePurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);

        mRecyclerViewGroupMembers = findViewById(R.id.purchisersReciclerView);

        mSpinnerGroups = findViewById(R.id.spn_groupName);
        mSpinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMembers((Group) mSpinnerGroups.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerCurrency = findViewById(R.id.spn_currency);

        mEditTextCost = findViewById(R.id.txt_cost);

        mButtonCreatePurchase = findViewById(R.id.btn_createPurchase);
        mButtonCreatePurchase.setOnClickListener(this);

        getData();
    }

    private void getData() {
        TokenManager tokenManager = new TokenManager(this);
        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<List<Group>> call = client.getUserGroups(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    groups = response.body();
                    buildGroupsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al obtener grupos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al obtener grupos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildGroupsAdapter() {
        groupArrayAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_item, groups);
        groupArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGroups.setAdapter(groupArrayAdapter);
    }

    private void getMembers(Group group) {
        TokenManager tokenManager = new TokenManager(this);

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getGroupMembers(group.getId());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    members = response.body();
                    participants = members;
                    buildRecyclerViewCreateGroupFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreatePurchaseActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreatePurchaseActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void loadCurrency() {
        //TODO: LLENAR EL SPINNER CON LAS MONEDAS QUE ACEPTAMOS
    }

    @Override
    public void onClick(View v) {
        createPurchase();
    }

    private void createPurchase() {

        //TODO: FALTA LA IMAGEN Y LA MONEDA QUE VIENE DEL BACKEND
        TokenManager tokenManager = new TokenManager(this);

        Purchase purchase = new Purchase();
        purchase.setCost(mEditTextCost.getText().toString());
        purchase.setCurrency(mSpinnerCurrency.getSelectedItem().toString());
        purchase.setGroup(((Group)mSpinnerGroups.getSelectedItem()).getId());

        List<String> participantsString = new ArrayList<String>();
        for (int i = 0; i < participants.size(); i++) participantsString.add(participants.get(i).getId());

        purchase.setParticipants(participantsString);
        purchase.setPurchaser(tokenManager.getUserIdFromToken());
    }
}
