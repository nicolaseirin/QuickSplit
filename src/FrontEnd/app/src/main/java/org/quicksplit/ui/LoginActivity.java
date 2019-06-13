package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.models.Login;
import org.quicksplit.models.Token;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String token;
    private TextInputLayout mLabelErrorUserName;
    private EditText mTextUserName;
    private TextInputLayout mLabelErrorPassword;
    private EditText mTextPassword;
    private Button mButtonLogin;
    private TextView mTextViewRegister;
    private TextView mLabelErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLabelErrorUserName = findViewById(R.id.lblError_txtUserName);
        mTextUserName = findViewById(R.id.txtUserName);

        mLabelErrorPassword = findViewById(R.id.lblError_txtPassword);
        mTextPassword = findViewById(R.id.txtPassword);

        mTextViewRegister = this.findViewById(R.id.txtView_register);
        mTextViewRegister.setOnClickListener(this);

        mButtonLogin = this.findViewById(R.id.btn_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        mLabelErrorMessage = this.findViewById(R.id.lbl_errorMessage);
    }

    private void login() {

        Login login = new Login();

        login.setMail(mTextUserName.getText().toString());
        login.setPassword(mTextPassword.getText().toString());

        if (!validateFieldsAndShowErrors())
            return;

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<Token> call = client.login(login);

        final ProgressDialog loading = ProgressDialog.show(this, "Recuperando datos", "Espere...", false, false);

        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    storageTokenAccess(response);
                    loading.dismiss();

                } else {
                    loading.dismiss();
                    showErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storageTokenAccess(Response<Token> response) {
        token = response.body().getToken();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.commit();

        redirect();
    }

    private void redirect() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private void showErrorMessage(Response<Token> response) {
        try {
            String errorMessage = response.errorBody().string();
            mLabelErrorMessage.setVisibility(View.VISIBLE);
            mLabelErrorMessage.setText(errorMessage);
        } catch (Exception e) {
            mLabelErrorMessage.setVisibility(View.VISIBLE);
            mLabelErrorMessage.setText(e.getMessage());
        }
    }

    private boolean validateFieldsAndShowErrors() {

        boolean isValid = false;

        String userName = mTextUserName.getText().toString();
        String password = mTextPassword.getText().toString();

        if (userName.isEmpty()) {
            mLabelErrorUserName.setError("El correo electrónico es requerido.");
            isValid = false;
        } else {
            mLabelErrorUserName.setError("");
            isValid = true;
        }

        if (password.isEmpty()) {
            mLabelErrorPassword.setError("La contraseña es requerida.");
            isValid = false;
        } else {
            mLabelErrorPassword.setError("");
            isValid = true;
        }

        return isValid;
    }

    @Override
    public void onClick(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
    }
}