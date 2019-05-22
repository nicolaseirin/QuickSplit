package org.quicksplit.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import org.quicksplit.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar = getSupportActionBar();
        //toolbar.setTitle(R.string.title_purchases);

        loadFragment(new PurchasesFragment());

        //setContentView(R.layout.search);

        SearchView search = findViewById(R.id.search_bar);
        search.setOnQueryTextListener(mOnQueryTextListener);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_botton);
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
            switch (item.getItemId()) {
                case R.id.navigation_purchases:
                    //toolbar.setTitle(R.string.title_purchases);
                    fragment = new PurchasesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_friends:
                    //toolbar.setTitle(R.string.title_friends);
                    fragment = new FriendsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_groups:
                    //toolbar.setTitle(R.string.title_groups);
                    //fragment = new CartFragment();
                    //loadFragment(fragment);
                    return true;
                case R.id.navigation_settings:
                    //toolbar.setTitle(R.string.title_settings);
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}