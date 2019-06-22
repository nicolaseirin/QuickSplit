package org.quicksplit.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.quicksplit.R;
import org.quicksplit.ServiceGenerator;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private String token;
    private String userId;

    private Button mButtonModifyData;
    private Button mButtonDeleteAccount;
    private Button mButtonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mButtonModifyData = findViewById(R.id.btn_modify);
        mButtonModifyData.setOnClickListener(this);

        mButtonDeleteAccount = findViewById(R.id.btn_delete);
        mButtonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryDeleteAccount();
            }
        });

        mButtonLogout = findViewById(R.id.btn_logout);
        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryLogout();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void tryLogout() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        logout();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Quiere cerrar la sesión?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void logout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.commit();

        redirectToLogin();
    }

    private void tryDeleteAccount() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteAccount();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("¿Quiere borrar su cuenta? Esta acción es inrreversible y se perderán todos los datos.").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteAccount() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        token = preferences.getString("token", null);

        JWT parsedJWT = new JWT(token);
        Claim subscriptionMetaData = parsedJWT.getClaim("Id");
        userId = subscriptionMetaData.asString();

        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<Void> call = client.deleteUser(userId);

        final ProgressDialog loading = ProgressDialog.show(SettingsActivity.this, "Recuperando datos", "Espere...", false, false);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    logout();
                    loading.dismiss();
                } else {
                    loading.dismiss();
                    Toast.makeText(SettingsActivity.this, getString(R.string.error_delete_account), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(SettingsActivity.this, "Error en la conexión al borrar la cuenta.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SettingsActivity.this, ModifyUserActivity.class);
        startActivity(intent);
    }
}
