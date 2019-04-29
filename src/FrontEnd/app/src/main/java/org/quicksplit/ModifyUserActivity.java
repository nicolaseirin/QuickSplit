package org.quicksplit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyUserActivity extends AppCompatActivity {

    private String token;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);
        getUserData();
    }

    private void getUserData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModifyUserActivity.this);
        token = preferences.getString("token", null);

        JWT parsedJWT = new JWT(token);
        Claim subscriptionMetaData = parsedJWT.getClaim("Id");
        String parsedValue = subscriptionMetaData.asString();

        UserClient client = ServiceGenerator.createService(UserClient.class, token);
        Call<User> call = client.getUser("users/" + parsedValue);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
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


}
