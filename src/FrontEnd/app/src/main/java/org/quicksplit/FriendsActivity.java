package org.quicksplit;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.quicksplit.model.Token;
import org.quicksplit.service.UserClient;

import java.util.ArrayList;

import retrofit2.Call;

public class FriendsActivity extends ListActivity {

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
        UserClient client = ServiceGenerator.createService(UserClient.class, "token");
    }
}
