package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.AddFriendsAdapter;
import org.quicksplit.adapters.DeleteFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.User;
import org.quicksplit.service.GroupClient;
import org.quicksplit.service.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private List<User> members;
    private List<User> friends;
    private List<String> friendsSelected;

    private Button mButtonModifyGroup;
    private EditText mEditTextGroupName;

    private TextView mLabelErrorFriendsSelected;
    private TextInputLayout mLabelErrorGroupName;

    private RecyclerView mRecyclerViewMembers;
    private DeleteFriendsAdapter mRecycleViewDeleteFriendsAdapter;

    private RecyclerView mRecyclerViewFriends;
    private AddFriendsAdapter mRecycleViewGroupFriendsAdapter;

    private RecyclerView.LayoutManager mRecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        friendsSelected = new ArrayList<String>();

        mEditTextGroupName = findViewById(R.id.txt_groupName);
        mRecyclerViewMembers = findViewById(R.id.membersReciclerView);
        mRecyclerViewFriends = findViewById(R.id.friendsReciclerView);

        mButtonModifyGroup = findViewById(R.id.btn_editGroup);
        mButtonModifyGroup.setOnClickListener(this);

        loadGroupData();
    }

    private void loadGroupData() {
        String groupId = getIntent().getStringExtra("EXTRA_GROUP_ID");
        getGroupFromId(groupId);
    }

    private void getGroupFromId(String groupId) {
        TokenManager tokenManager = new TokenManager(this);

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<Group> call = client.getGroup(groupId);

        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    loadFields(response.body());
                } else {

                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {

            }
        });
    }

    private void loadFields(Group group) {
        mEditTextGroupName.setText(group.getName());
        getGroupMembers();
        getFriends();
    }

    private void getGroupMembers() {
        TokenManager tokenManager = new TokenManager(this);

        GroupClient client = ServiceGenerator.createService(GroupClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getGroupMembers(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    members = response.body();
                    buildRecyclerViewDeleteFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyGroupActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ModifyGroupActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void buildRecyclerViewAddFriendsAdapter() {
        friends.removeAll(members);

        mRecyclerViewFriends.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewGroupFriendsAdapter = new AddFriendsAdapter(friends);
        mRecyclerViewFriends.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewFriends.setAdapter(mRecycleViewGroupFriendsAdapter);
    }

    private void buildRecyclerViewDeleteFriendsAdapter() {
        mRecyclerViewMembers.setHasFixedSize(true);
        mRecyclerViewManager = new LinearLayoutManager(this);
        mRecycleViewDeleteFriendsAdapter = new DeleteFriendsAdapter(members);
        mRecyclerViewMembers.setLayoutManager(mRecyclerViewManager);
        mRecyclerViewMembers.setAdapter(mRecycleViewDeleteFriendsAdapter);
    }

    private void getFriends() {
        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<List<User>> call = client.getFriends(tokenManager.getUserIdFromToken());

        final ProgressDialog loading = ProgressDialog.show(this, "Fetching Data", "Please wait...", false, false);

        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    friends = response.body();
                    buildRecyclerViewAddFriendsAdapter();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(ModifyGroupActivity.this, "Error al obtener amigos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(ModifyGroupActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
