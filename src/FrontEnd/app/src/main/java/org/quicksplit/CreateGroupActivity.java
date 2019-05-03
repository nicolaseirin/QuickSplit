package org.quicksplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.quicksplit.model.GroupModelIn;
import org.quicksplit.service.GroupClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTextName;
    private EditText mTextAdmin;
    private Button createGroupButton;
    ArrayList<String> users;
    RecyclerView recycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        recycler = (RecyclerView) findViewById(R.id.RecyclerUsersId);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        users = new ArrayList<String>();

        /*foreach(s in usersFfromService)
            users.add(s.Name);*/


        mTextName = (EditText) findViewById(R.id.txtGroupName);

        createGroupButton = (Button) findViewById(R.id.btn_register);

        createGroupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        GroupModelIn group = new GroupModelIn();

        group.setName(mTextName.getText().toString());

        GroupClient client = ServiceGenerator.createService(GroupClient.class);
        Call<GroupModelIn> call = client.createGroup(group);
        call.enqueue(new Callback<GroupModelIn>() {

            @Override
            public void onResponse(Call<GroupModelIn> call, Response<GroupModelIn> response) {
                Toast.makeText(CreateGroupActivity.this, "Grupo creado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<GroupModelIn> call, Throwable t) {
                Toast.makeText(CreateGroupActivity.this, "Error al crear grupo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
