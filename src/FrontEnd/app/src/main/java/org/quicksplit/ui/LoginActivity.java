package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.TokenManager;
import org.quicksplit.models.Login;
import org.quicksplit.models.Token;
import org.quicksplit.models.User;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static String token;
    private TextInputLayout mLabelErrorUserName;
    private EditText mTextUserName;
    private TextInputLayout mLabelErrorPassword;
    private EditText mTextPassword;
    private Button mButtonLogin;
    private TextView mTextViewRegister;
    private TextView mLabelErrorMessage;
    private GoogleSignInClient mGoogleSignInClient;

    private String name;
    private String lastName;
    private String email;
    private String password;
    private Uri uriAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        mLabelErrorUserName = findViewById(R.id.lblError_txtUserName);
        mTextUserName = findViewById(R.id.txtUserName);

        mLabelErrorPassword = findViewById(R.id.lblError_txtPassword);
        mTextPassword = findViewById(R.id.txtPassword);

        mTextViewRegister = this.findViewById(R.id.txtView_register);
        mTextViewRegister.setOnClickListener(this);

        mButtonLogin = this.findViewById(R.id.btn_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryLogin();
            }
        });

        mLabelErrorMessage = this.findViewById(R.id.lbl_errorMessage);
    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            name = account.getGivenName();
            lastName = account.getFamilyName();
            email = account.getEmail();
            password = account.getIdToken();

            uriAvatar = account.getPhotoUrl();

            User user = new User();
            user.setName(name);
            user.setLastName(lastName);
            user.setMail(email);
            user.setPassword(password);

            UserClient client = ServiceGenerator.createService(UserClient.class);
            Call<User> call = client.createAccount(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                        login();
                    } else if (response.code() == 400) {
                        login();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error al registrar Usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ApiException e) {
            mLabelErrorMessage.setVisibility(View.VISIBLE);
            mLabelErrorMessage.setText("Ocurrió un error durante el login. Por si lo necesitas el código de error es: " + e.getStatusCode());
        }
    }

    private void setGoogleAvatarImage() {
        TokenManager tokenManager = new TokenManager(this);

        UserClient client = ServiceGenerator.createService(UserClient.class, tokenManager.getToken());
        Call<Void> call = client.setUserAvatar(tokenManager.getUserIdFromToken(), uriAvatar.toString());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    private void tryLogin() {
        email = mTextUserName.getText().toString();
        password = mTextPassword.getText().toString();

        login();
    }

    private void login() {

        Login login = new Login();

        login.setMail(email);
        login.setPassword(password);

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
                    if (uriAvatar != null)
                        setGoogleAvatarImage();
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

        if (email.isEmpty()) {
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