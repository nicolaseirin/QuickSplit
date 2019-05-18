package org.quicksplit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends Activity {

    private String token;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getUserListItems();
    }

    private void getUserListItems() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FriendsActivity.this);
        token = preferences.getString("token", null);
        UserClient client = ServiceGenerator.createService(UserClient.class, token);

        Call<List<User>> call = client.getUsers();
        call.enqueue(new Callback<List<User>>() {

            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                } else {
                    Toast.makeText(FriendsActivity.this, "Error al obtener usuarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(FriendsActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
