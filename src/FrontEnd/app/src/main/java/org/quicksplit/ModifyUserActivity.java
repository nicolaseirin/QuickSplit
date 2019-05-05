package org.quicksplit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyUserActivity extends AppCompatActivity implements View.OnClickListener {

    private String token;
    private String userId;
    private User user;

    private TextInputLayout mLabelErrorName;
    private EditText mTextName;
    private TextInputLayout mLabelErrorLastName;
    private EditText mTextLastName;
    private TextInputLayout mLabelErrorEmail;
    private EditText mTextEmail;
    private TextInputLayout mLabelErrorPassword;
    private EditText mTextPassword;
    private TextInputLayout mLabelErrorRepeatPassword;
    private EditText mTextRepeatPassword;
    private TextView mLabelErrorMessage;
    private Button mButtonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        mLabelErrorName = (TextInputLayout) findViewById(R.id.lblError_txtName);
        mTextName = (EditText) findViewById(R.id.txtName);

        mLabelErrorLastName = (TextInputLayout) findViewById(R.id.lblError_txtLastName);
        mTextLastName = (EditText) findViewById(R.id.txtLastName);

        mLabelErrorEmail = (TextInputLayout) findViewById(R.id.lblError_txtEmail);
        mTextEmail = (EditText) findViewById(R.id.txtEmail);

        mLabelErrorPassword = (TextInputLayout) findViewById(R.id.lblError_txtPassword);
        mTextPassword = (EditText) findViewById(R.id.txtPassword);

        mLabelErrorRepeatPassword = (TextInputLayout) findViewById(R.id.lblError_txtRepeatPassword);
        mTextRepeatPassword = (EditText) findViewById(R.id.txtRepeatPassword);

        mLabelErrorMessage = (TextView) findViewById(R.id.lbl_errorMessage);

        mButtonSave = (Button) findViewById(R.id.btn_saveChanges);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateUserData();
            }
        });

        getUserData();
    }

    private void getUserData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModifyUserActivity.this);
        token = preferences.getString("token", null);

        JWT parsedJWT = new JWT(token);
        Claim subscriptionMetaData = parsedJWT.getClaim("Id");
        userId = subscriptionMetaData.asString();

        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<User> call = client.getUser(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    loadUserData();
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Error al solicitar la edición de datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error al solicitar la edición de datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        mTextName.setText(user.getName());
        mTextLastName.setText(user.getLastName());
        mTextEmail.setText(user.getMail());
    }

    private void updateUserData() {
        User newUser = new User();
        newUser.setName(mTextName.getText().toString());
        newUser.setLastName(mTextLastName.getText().toString());
        newUser.setMail(mTextEmail.getText().toString());
        newUser.setPassword(mTextPassword.getText().toString());

        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<User> call = client.editUser(userId, newUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ModifyUserActivity.this, "Datos modificados correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyUserActivity.this, "Error al intentar modificar datos.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ModifyUserActivity.this, "Error al modificar datos del usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        updateUserData();
    }
}
