package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Toast;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.adapters.GroupFriendsAdapter;
import org.quicksplit.models.Group;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity {

    private List<User> friends;
    private List<String> friendsSelected;
    private EditText mEditTextGroupName;
    private RecyclerView mRecyclerViewFriends;
    private GroupFriendsAdapter mRecycleViewGroupFriendsAdapter;
    private RecyclerView.LayoutManager mRecyclerViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mEditTextGroupName = findViewById(R.id.txt_GroupName);
        mRecyclerViewFriends = findViewById(R.id.friendsReciclerView);

        getFriends();
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
        mRecycleViewGroupFriendsAdapter.setOnItemClickListener(new GroupFriendsAdapter.OnItemCheakedListener() {
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
        TokenManager tokenManager = new TokenManager(this);

        Group group = new Group();

        group.setAdmin(tokenManager.getUserIdFromToken());
        group.setName(mEditTextGroupName.getText().toString());
        group.setMembers(friendsSelected);

        //TODO: LLAMAR AL BACK Y MANDAR EL GRUPO
    }
}

