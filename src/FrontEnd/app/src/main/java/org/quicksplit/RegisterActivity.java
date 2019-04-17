package org.quicksplit;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

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
    private Button mButtonregister;
    private TextView mTextViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        mButtonregister = (Button) findViewById(R.id.btn_register);
        mButtonregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser();
            }
        });

        mTextViewLogin = (TextView) findViewById(R.id.txtView_login);
        mTextViewLogin.setOnClickListener(this);
    }

    private void registerUser() {
        User user = new User();

        user.setName(mTextName.getText().toString());
        user.setLastName(mTextLastName.getText().toString());
        user.setMail(mTextEmail.getText().toString());
        user.setPassword(mTextPassword.getText().toString());

        if (!validateFieldsAndShowErrors())
            return;

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<User> call = client.createAccount(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                } else {
                    showErrorMessage(response);
                }
            }

            private void showErrorMessage(Response<User> response) {
                try {
                    String errorMessage = response.errorBody().string();
                    mLabelErrorMessage.setVisibility(View.VISIBLE);
                    mLabelErrorMessage.setText(errorMessage);
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error al registrar Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFieldsAndShowErrors() {

        boolean isValid = false;

        String name = mTextName.getText().toString();
        String lastName = mTextLastName.getText().toString();
        String email = mTextEmail.getText().toString();
        String password = mTextPassword.getText().toString();
        String repeatPassword = mTextRepeatPassword.getText().toString();

        if (name.isEmpty()) {
            mLabelErrorName.setError("El nombre es requerido.");
            isValid = false;
        } else {
            mLabelErrorName.setError("");
            isValid = true;
        }

        if (lastName.isEmpty()) {
            mLabelErrorLastName.setError("El apellido es requerido.");
            isValid = false;
        } else {
            mLabelErrorLastName.setError("");
            isValid &= true;
        }

        if (email.isEmpty()) {
            mLabelErrorEmail.setError("El email es requerido.");
            isValid = false;
        } else {
            mLabelErrorEmail.setError("");
            isValid &= true;
        }

        if (password.isEmpty()) {
            mLabelErrorPassword.setError("La contraseña es requerida.");
            isValid = false;
        } else {
            mLabelErrorPassword.setError("");
            isValid &= true;
        }

        if (!password.equals(repeatPassword)) {
            mLabelErrorRepeatPassword.setError("Las contraseñas no coinciden.");
            isValid = false;
        } else {
            mLabelErrorRepeatPassword.setError("");
            isValid &= true;
        }

        return isValid;
    }

    @Override
    public void onClick(View view) {
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }
}
