package org.quicksplit;

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

import org.quicksplit.model.Login;
import org.quicksplit.model.Token;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_main);

        mLabelErrorUserName = (TextInputLayout) findViewById(R.id.lblError_txtUserName);
        mTextUserName = (EditText) findViewById(R.id.txtUserName);

        mLabelErrorPassword = (TextInputLayout) findViewById(R.id.lblError_txtPassword);
        mTextPassword = (EditText) findViewById(R.id.txtPassword);

        mTextViewRegister = (TextView) this.findViewById(R.id.txtView_register);
        mTextViewRegister.setOnClickListener(this);

        mButtonLogin = (Button) this.findViewById(R.id.btn_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        mLabelErrorMessage = (TextView) this.findViewById(R.id.lbl_errorMessage);
    }

    private void login() {

        Login login = new Login();

        login.setMail(mTextUserName.getText().toString());
        login.setPassword(mTextPassword.getText().toString());

        if (!validateFieldsAndShowErrors())
            return;

        UserClient client = ServiceGenerator.createService(UserClient.class);
        Call<Token> call = client.login(login);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    token = response.body().getToken();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.commit();
                } else {
                    showErrorMessage(response);
                }
            }

            private void showErrorMessage(Response<Token> response) {
                try {
                    String errorMessage = response.errorBody().string();
                    mLabelErrorMessage.setVisibility(View.VISIBLE);
                    mLabelErrorMessage.setText(errorMessage);
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
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
        startActivity(registerIntent);
    }
}
