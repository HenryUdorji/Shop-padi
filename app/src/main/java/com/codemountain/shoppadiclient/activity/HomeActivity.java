package com.codemountain.shoppadiclient.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.fragments.HomeFragment;
import com.codemountain.shoppadiclient.utils.BottomNavHelper;
import com.codemountain.shoppadiclient.utils.SharedPref;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private BottomNavigationView bottomNavigationView;
    private SharedPref sharedPref;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "onCreate: " +" Activity Created");

        setupBottomNavigation();
        sharedPref = new SharedPref(this);

        //default fragment
        HomeFragment homeFragment= new HomeFragment();
        manager = this.getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.container, homeFragment, "HOME");
        transaction.commit();

    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNav);
        BottomNavHelper.enableNavigation(HomeActivity.this, bottomNavigationView);
        Log.d(TAG, "setupBottomNavigation: " + "Created");
    }

    private void checkUserStatus(){
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            //user is signed in
            //email = currentUser.getEmail();
            String uid = currentUser.getUid();
            /*preferences = getSharedPreferences("UID", MODE_PRIVATE);
            editor = preferences.edit();
            editor.putString("UID", uid);
            editor.apply();*/
            sharedPref.putStringInPref("UID", uid);

        }
        else {
            startActivity(new Intent(HomeActivity.this, CredentialsActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onResume() {
        //checkOnlineStatus("online");
        checkUserStatus();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out!");
        builder.setMessage("Are you sure you want to log out.");
        builder.setPositiveButton("Log out", (dialog, which) -> {
            mAuth.signOut();
            checkUserStatus();
        }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}
