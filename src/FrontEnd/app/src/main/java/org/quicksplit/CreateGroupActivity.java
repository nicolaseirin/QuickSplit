package org.quicksplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.quicksplit.model.GroupModelIn;
import org.quicksplit.service.GroupClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTextName;
    private EditText mTextAdmin;
    private Button createGroupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        mTextName = (EditText) findViewById(R.id.txtName);
        mTextAdmin = (EditText) findViewById(R.id.txtAdmin);

        createGroupButton = (Button) findViewById(R.id.btn_register);

        createGroupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        GroupModelIn group = new GroupModelIn();

        group.setName(mTextName.getText().toString());
        group.setAdmin(mTextAdmin.getText().toString());

        GroupClient client = ServiceGenerator.createService(GroupClient.class);
        Call<GroupModelIn> call = client.createAccount(group);
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
