package org.quicksplit;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.quicksplit.model.Token;
import org.quicksplit.model.User;
import org.quicksplit.service.UserClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends ListActivity {

    private String token;
    private ListView listFriends;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        listFriends = (ListView) findViewById(R.id.listFriends);
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
                    //storageTokenAccess(response);

                } else {
                    //showErrorMessage(response);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(FriendsActivity.this, "Error en la comunicación al obtener usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
