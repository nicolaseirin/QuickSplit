package org.quicksplit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.quicksplit.model.UserModelIn;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTextName;
    private EditText mTextLastName;
    private EditText mTextEmail;
    private EditText mTextPassword;
    private EditText mTextRepeatPassword;
    private Button mButtonregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTextName = (EditText) findViewById(R.id.txtName);
        mTextLastName = (EditText) findViewById(R.id.txtLastName);
        mTextEmail = (EditText) findViewById(R.id.txtEmail);
        mTextPassword = (EditText) findViewById(R.id.txtPassword);
        mTextRepeatPassword = (EditText) findViewById(R.id.txtRepeatPassword);
        mButtonregister = (Button) findViewById(R.id.btn_register);

        mButtonregister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        UserModelIn user = new UserModelIn();

        user.setName(mTextName.toString());
        user.setLastName(mTextLastName.toString());
        user.setEmail(mTextEmail.toString());
        user.setPassword(mTextPassword.toString());

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<UserModelIn> call = client.createAccount(user);
        call.enqueue(new Callback<UserModelIn>() {
            @Override
            public void onResponse(Call<UserModelIn> call, Response<UserModelIn> response) {
                Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<UserModelIn> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error al registrar Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
