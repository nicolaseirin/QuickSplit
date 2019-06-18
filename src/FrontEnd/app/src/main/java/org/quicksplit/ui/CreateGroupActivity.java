package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.GroupModelIn;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private List<User> friends;
    private List<String> friendsSelected;
    private Button mButtonCreateGroup;

    private Toolbar mToolbar;

    private TextView mLabelErrorFriendsSelected;
    private TextInputLayout mLabelErrorGroupName;
    private EditText mEditTextGroupName;
    private RecyclerView mRecyclerViewFriends;
    private GroupFriendsAdapter mRecycleViewGroupFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        friendsSelected = new ArrayList<String>();

        mLabelErrorFriendsSelected = findViewById(R.id.lbl_errorMessage);

        mLabelErrorGroupName = findViewById(R.id.lblError_txtGroupName);
        mEditTextGroupName = findViewById(R.id.txt_GroupName);

        mRecyclerViewFriends = findViewById(R.id.friendsReciclerView);

        mButtonCreateGroup = findViewById(R.id.btn_createGroup);
        mButtonCreateGroup.setOnClickListener(this);


        getFriends();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.done) {
            createGroup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFriends() {
        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getFriends(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    friends = response.body();
                    buildRecyclerViewCreateGroupFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreateGroupActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreateGroupActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildRecyclerViewCreateGroupFriendsAdapter() {
        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupFriendsAdapter = new GroupFriendsAdapter(friends);
        mRecyclerViewFriends.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewFriends.setAdapter(mRecycleViewGroupFriendsAdapter);
        mRecycleViewGroupFriendsAdapter.setOnItemClickListener(new GroupFriendsAdapter.OnItemCheckedListener() {
            @Override
            public void onCheck(User user) {
                friendsSelected.add(user.getId());
            }

            @Override
            public void onUncheck(User user) {
                friendsSelected.remove(user.getId());
            }
        });
    }

    private void createGroup() {
        showErrors();

        if (!validFileds())
            return;

        TokenManager tokenManager = new TokenManager(this);

        Group group = new Group();

        group.setAdmin(tokenManager.getUserIdFromToken());
        group.setName(mEditTextGroupName.getText().toString());
        group.setMemberships(friendsSelected);

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<GroupModelIn> call = client.createGroup(group);

        final ProgressDialog loading = ProgressDialog.show(CreateGroupActivity.this, getString(R.string.fetching_data), getString(R.string.please_wait), false, false);

        call.enqueue(new Callback<GroupModelIn>() {

            @Override
            public void onResponse(Call<GroupModelIn> call, Response<GroupModelIn> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    loading.dismiss();
                    Toast.makeText(CreateGroupActivity.this, getString(R.string.error_add_group) + " " + response.code(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(Call<GroupModelIn> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(CreateGroupActivity.this, "Error en la comunicación al crear grupo.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
        });
    }

    private void showErrors() {
        if (mEditTextGroupName.getText().toString().trim().equals("")) {
            mLabelErrorGroupName.setVisibility(View.VISIBLE);
            mLabelErrorGroupName.setError("El grupo debe tener un nombre no vacío.");
        } else {
            mLabelErrorGroupName.setVisibility(View.GONE);
            mLabelErrorGroupName.setError("");
        }

        if (friendsSelected.isEmpty()) {
            mLabelErrorFriendsSelected.setVisibility(View.VISIBLE);
            mLabelErrorFriendsSelected.setText("Debe seleccionar por lo menos un amigo para crear un grupo.");
        } else {
            mLabelErrorFriendsSelected.setVisibility(View.GONE);
            mLabelErrorFriendsSelected.setText("");
        }
    }

    private boolean validFileds() {
        return !friendsSelected.isEmpty() && !mEditTextGroupName.getText().toString().trim().equals("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.confirmation, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        createGroup();
    }
}

