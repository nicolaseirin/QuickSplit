package org.quicksplit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();

        //load the purchases fragment by default
        toolbar.setTitle(R.string.title_purchases);
        loadFragment(new PurchasesFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

<<<<<<< HEAD
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_purchases:
                    toolbar.setTitle(R.string.title_purchases);
                    fragment = new PurchasesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_friends:
                    toolbar.setTitle(R.string.title_friends);
                    fragment = new FriendsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_groups:
                    toolbar.setTitle(R.string.title_groups);
                    //fragment = new CartFragment();
                    //loadFragment(fragment);
                    return true;
                case R.id.navigation_settings:
                    toolbar.setTitle(R.string.title_settings);
                    //fragment = new ProfileFragment();
                    //loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
=======
    @Override
    public void onClick(View v) {
        //Intent modifyUser = new Intent(this, ModifyUserActivity.class);
        //startActivity(modifyUser);
        Intent createGroup = new Intent(this, CreateGroupActivity.class);
        startActivity(createGroup);
>>>>>>> feature/CreacionDeGrupo
    }
}