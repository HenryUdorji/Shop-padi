package com.codemountain.shoppadiclient.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codemountain.shoppadiclient.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShippingAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference customerRef;
    private String uid;
    private Button saveShippingAddressBtn;
    private EditText nameEdit, phoneNumberEdit, homeAddressEdit, stateEdit, cityEdit;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        customerRef = FirebaseDatabase.getInstance().getReference().child("Customers");
        customerRef.keepSynced(true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveShippingAddressBtn = findViewById(R.id.saveShippingAddressBtn);
        nameEdit = findViewById(R.id.nameEdit);
        phoneNumberEdit = findViewById(R.id.phoneNumberEdit);
        homeAddressEdit = findViewById(R.id.addressEdit);
        stateEdit = findViewById(R.id.stateEdit);
        cityEdit = findViewById(R.id.cityEdit);
        progressBar = findViewById(R.id.progressBar);

        saveShippingAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdit.getText().toString();
                String phoneNumber = phoneNumberEdit.getText().toString();
                String homeAddress = homeAddressEdit.getText().toString();
                String state = stateEdit.getText().toString();
                String city = cityEdit.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(homeAddress)
                    || TextUtils.isEmpty(state) || TextUtils.isEmpty(city)){
                    Toast.makeText(ShippingAddressActivity.this, "Fields cannot be empty!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveShippingAddress(name, phoneNumber, homeAddress, state, city);
                }
            }
        });


        getAddress();
    }

    private void saveShippingAddress(String name, String phoneNumber, String homeAddress, String state, String city) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> shippingHashMap = new HashMap<>();
        shippingHashMap.put("name", name);
        shippingHashMap.put("phone_number", phoneNumber);
        shippingHashMap.put("home_address", homeAddress);
        shippingHashMap.put("state", state);
        shippingHashMap.put("city", city);

        customerRef.child(uid).updateChildren(shippingHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ShippingAddressActivity.this, "Shipping Address Updated", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShippingAddressActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getAddress() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = customerRef.orderByChild("email").equalTo(currentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("name").getValue().toString();
                        String email = ds.child("email").getValue().toString();
                        String address = ds.child("home_address").getValue().toString();
                        String phoneNumber = ds.child("phone_number").getValue().toString();
                        String state = ds.child("state").getValue().toString();
                        String city = ds.child("city").getValue().toString();

                        homeAddressEdit.setText(address);
                        nameEdit.setText(name);
                        phoneNumberEdit.setText(phoneNumber);
                        cityEdit.setText(city);
                        stateEdit.setText(state);


                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShippingAddressActivity.this, "Failed to get details \n"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(ShippingAddressActivity.this, HomeActivity.class));
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
            startActivity(new Intent(ShippingAddressActivity.this, CredentialsActivity.class));
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