package com.codemountain.shoppadiclient.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.utils.NetworkManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CredentialsActivity extends AppCompatActivity {

    private Button loginBtn, registerBtn;
    private FirebaseAuth mAuth;

    //dialog login widget
    private EditText dialogEmailEdit, dialogPasswordEdit, dialogNameReg;
    private Button dialogLoginBtn, dialogRegBtn;
    private ProgressBar dialogProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> showLoginDialog());
        registerBtn.setOnClickListener(v -> showRegisterDialog());
    }

    private void showLoginDialog() {
        Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.dialog_login);
        loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEmailEdit = loginDialog.findViewById(R.id.dialogEmailEdit);
        dialogPasswordEdit = loginDialog.findViewById(R.id.dialogPasswordEdit);
        dialogLoginBtn = loginDialog.findViewById(R.id.dialogLoginBtn);
        dialogProgressBar = loginDialog.findViewById(R.id.dialogProgressBar);


        dialogLoginBtn.setOnClickListener(v -> {
            String email = dialogEmailEdit.getText().toString();
            String password = dialogPasswordEdit.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(CredentialsActivity.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            }
            else {
                if (NetworkManager.isConnectToInternet(CredentialsActivity.this)){
                    loginCustomer(email, password);
                }
                else {
                    Toast.makeText(this, R.string.turn_on_internet, Toast.LENGTH_LONG).show();
                }

            }
        });
        loginDialog.show();
    }

    private void loginCustomer(String email, String password) {
        dialogProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        dialogProgressBar.setVisibility(View.GONE);
                        //FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(CredentialsActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CredentialsActivity.this, HomeActivity.class));
                        finish();
                        CredentialsActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(CredentialsActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        dialogProgressBar.setVisibility(View.GONE);
                    }

                });
    }

    private void showRegisterDialog() {
        Dialog registerDialog = new Dialog(this);
        registerDialog.setContentView(R.layout.dialog_register);
        registerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEmailEdit = registerDialog.findViewById(R.id.dialogEmailEdit);
        dialogPasswordEdit = registerDialog.findViewById(R.id.dialogPasswordEdit);
        dialogNameReg = registerDialog.findViewById(R.id.dialogNameEdit);
        dialogRegBtn = registerDialog.findViewById(R.id.dialogRegBtn);
        dialogProgressBar = registerDialog.findViewById(R.id.dialogProgressBar);


        dialogRegBtn.setOnClickListener(v -> {
            String email = dialogEmailEdit.getText().toString();
            String password = dialogPasswordEdit.getText().toString();
            String name = dialogNameReg.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)){
                Toast.makeText(CredentialsActivity.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            }
            else if (password.length() <= 6){
                Toast.makeText(CredentialsActivity.this, "Password length is too short", Toast.LENGTH_SHORT).show();
            }
            else {
                if (NetworkManager.isConnectToInternet(CredentialsActivity.this)){
                    registerCustomer(email, password, name);
                }
                else {
                    Toast.makeText(this, R.string.turn_on_internet, Toast.LENGTH_LONG).show();
                }

            }
        });
        registerDialog.show();
    }

    private void registerCustomer(String email, String password, final String name) {

        dialogProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            dialogProgressBar.setVisibility(View.GONE);
                            final String currentDate = String.valueOf(System.currentTimeMillis());
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (currentUser != null) {
                                String email = currentUser.getEmail();
                                String customerUID = currentUser.getUid();

                                HashMap<String, Object> regHashMap = new HashMap<>();
                                regHashMap.put("email", email);
                                regHashMap.put("customer_uid", customerUID);
                                regHashMap.put("name", name);
                                regHashMap.put("phone_number", "00000000000");
                                regHashMap.put("image", "");
                                regHashMap.put("registration_date", currentDate);
                                regHashMap.put("home_address", "127, Church Rd, Idimu");
                                regHashMap.put("state", "Lagos");
                                regHashMap.put("city", "Alimosho");
                                regHashMap.put("Shopping_points", "0");

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customers");
                                reference.child(customerUID).setValue(regHashMap);
                                Toast.makeText(CredentialsActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(CredentialsActivity.this, HomeActivity.class));
                                finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CredentialsActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialogProgressBar.setVisibility(View.GONE);
                        Toast.makeText(CredentialsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
