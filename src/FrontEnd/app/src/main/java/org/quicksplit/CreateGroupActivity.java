package org.quicksplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.quicksplit.model.GroupModelIn;
import org.quicksplit.service.GroupClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTextName;
    private EditText mTextAdmin;
    private ArrayList<String> members;
    private ListView users;
    private Button createGroupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mTextName = (EditText) findViewById(R.id.txtGroupName);
        users = (ListView) findViewById(R.id.usersList);

        ArrayList<String> aux = new ArrayList<>();
        aux.add("Juan");
        aux.add("Pedro");
        aux.add("PEpe");

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,aux);

        users.setAdapter(adapter);

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
