package org.quicksplit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.quicksplit.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar_top);
        mToolbar.setTitle(R.string.title_purchases);

        setSupportActionBar(mToolbar);

        actionBar = MainActivity.this.getSupportActionBar();

        loadFragment(new PurchasesFragment(), R.id.navigation_purchases);

        BottomNavigationView navigation = findViewById(R.id.navigation_botton);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            return createFragmetView(item.getItemId());
        }
    };

    public void refreshFragment(String fragmentId) {
        if (fragmentId != null) {
            int intFramentId = Integer.parseInt(fragmentId);
            createFragmetView(intFramentId);
        }
    }

    private boolean createFragmetView(int fragmentId) {
        Fragment fragment;
        switch (fragmentId) {
            case R.id.navigation_purchases:
                mToolbar.setTitle(R.string.title_purchases);
                actionBar.show();
                fragment = new PurchasesFragment();
                loadFragment(fragment, R.id.navigation_purchases);
                return true;
            case R.id.navigation_friends:
                mToolbar.setTitle(R.string.title_friends);
                actionBar.hide();
                fragment = new FriendsFragment();
                loadFragment(fragment, R.id.navigation_friends);
                return true;
            case R.id.navigation_groups:
                actionBar.show();
                mToolbar.setTitle(R.string.title_groups);
                fragment = new GroupsFragment();
                loadFragment(fragment, R.id.navigation_groups);
                return true;
        }

        return false;
    }

    private void loadFragment(Fragment fragment, int fragmentId) {

        Bundle data = new Bundle();
        data.putString("fragment_id", fragmentId + "");
        fragment.setArguments(data);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}