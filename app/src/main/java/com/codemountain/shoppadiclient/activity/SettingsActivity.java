package com.codemountain.shoppadiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.codemountain.shoppadiclient.utils.SharedPref.MODE;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Toolbar toolbar;
    private SwitchCompat modeSwitch;
    private TextView selectedModeText, nightModeText;
    private boolean mode;
    private SharedPref sharedPref;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: UID " + uid);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        modeSwitch = findViewById(R.id.modeSwitch);
        selectedModeText = findViewById(R.id.modeSelectedText);
        nightModeText = findViewById(R.id.nightModeText);

        sharedPref = new SharedPref(this);
        mode = sharedPref.getBoolean();

        setModeSwitch();
    }

    private void setModeSwitch() {
        if (mode){
            modeSwitch.setChecked(true);
            selectedModeText.setText("Dark mode");
            nightModeText.setText("Enable Light mode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                sharedPref.putBooleanInPref(MODE, true);
                selectedModeText.setText("Dark mode");
                nightModeText.setText("Enable Light mode");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                sharedPref.putBooleanInPref(MODE, false);
                selectedModeText.setText("Light mode");
                nightModeText.setText("Enable Dark mode");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus(){
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            //user is signed in
            //email = currentUser.getEmail();
            uid = currentUser.getUid();
        }
        else {
            startActivity(new Intent(SettingsActivity.this, CredentialsActivity.class));
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
}
