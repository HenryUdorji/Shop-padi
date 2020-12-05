package com.codemountain.shoppadiclient.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.utils.NetworkManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText, homeAddressText, cityText, stateText, phoneNumberText, changeAddressText, grandTotalText;
    private Button placeOrderBtn;
    private CardView cardSelectPaymentMethod;
    private ProgressBar toolBarProgress;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference customerRef, ordersRef, cartRef;
    private String uid;
    private String totalPrice, productName, from;
    private int quantity;

    //dialog
    private RadioButton selectedRBtn;
    private RadioGroup radioGroup;
    private Button confirmPaymentMethodBtn;
    private String radioBtnText;

    private String name, email, home_address, phoneNumber, state, city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Intent intent = getIntent();
        totalPrice = intent.getStringExtra("TOTAL");
        productName = intent.getStringExtra("NAME");
        quantity = intent.getIntExtra("QUANTITY", 0);
        from = intent.getStringExtra("FROM");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        customerRef = FirebaseDatabase.getInstance().getReference().child("Customers");
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");
        customerRef.keepSynced(true);

        grandTotalText = findViewById(R.id.grandTotalText);
        toolBarProgress = findViewById(R.id.toolbarProgress);
        nameText = findViewById(R.id.customerName);
        homeAddressText = findViewById(R.id.homeAddress);
        cityText = findViewById(R.id.cityName);
        stateText = findViewById(R.id.stateName);
        phoneNumberText = findViewById(R.id.phoneNumber);
        changeAddressText = findViewById(R.id.changeAddressText);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        cardSelectPaymentMethod = findViewById(R.id.cardSelectPaymentMethod);

        grandTotalText.setText(totalPrice);

        placeOrderBtn.setOnClickListener(this);
        changeAddressText.setOnClickListener(this);
        cardSelectPaymentMethod.setOnClickListener(this);

        getAddress();
    }

    private void getAddress() {
        Query query = customerRef.orderByChild("email").equalTo(currentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    name = ds.child("name").getValue().toString();
                    email = ds.child("email").getValue().toString();
                    home_address = ds.child("home_address").getValue().toString();
                    phoneNumber = ds.child("phone_number").getValue().toString();
                    state = ds.child("state").getValue().toString();
                    city = ds.child("city").getValue().toString();

                    homeAddressText.setText(home_address);
                    nameText.setText(name);
                    phoneNumberText.setText(phoneNumber);
                    cityText.setText(city);
                    stateText.setText(state);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConfirmOrderActivity.this, "Failed to get details \n"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.placeOrderBtn:
                if (NetworkManager.isConnectToInternet(this)) {
                    placeOrder();
                }
                else {
                    Toast.makeText(this, R.string.turn_on_internet, Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.changeAddressText:
                startActivity(new Intent(ConfirmOrderActivity.this, ShippingAddressActivity.class));
                break;

            case R.id.cardSelectPaymentMethod:
                setupDialog();
                break;
        }
    }

    private void placeOrder() {
        toolBarProgress.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss: a");
        String currentTime = timeFormat.format(calendar.getTime());

        String orderID = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> orderHashMap = new HashMap<>();
        orderHashMap.put("name", name);
        orderHashMap.put("total_price", totalPrice);
        orderHashMap.put("home_address", home_address);
        orderHashMap.put("city", city);
        orderHashMap.put("state", state);
        orderHashMap.put("email", email);
        orderHashMap.put("phone_number", phoneNumber);
        orderHashMap.put("time", currentTime);
        orderHashMap.put("date", currentDate);
        orderHashMap.put("order_state", "not Shipped");
        orderHashMap.put("payment_method", radioBtnText);
        orderHashMap.put("order_ID", orderID);


        ordersRef.child(uid).child(orderID).updateChildren(orderHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                cartRef.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ConfirmOrderActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ConfirmOrderActivity.this, " "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        toolBarProgress.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ConfirmOrderActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                toolBarProgress.setVisibility(View.GONE);
            }
        });
    }

    private void setupDialog() {
        final Dialog paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.dialog_layout_payment);

        radioGroup = paymentMethodDialog.findViewById(R.id.radioGroup);
        confirmPaymentMethodBtn = paymentMethodDialog.findViewById(R.id.confirmPaymentMethodBtn);

        confirmPaymentMethodBtn.setOnClickListener(v -> {
            if (v == confirmPaymentMethodBtn){
                selectedRBtn = paymentMethodDialog.findViewById(radioGroup.getCheckedRadioButtonId());
                radioBtnText = selectedRBtn.getText().toString();

                Toast.makeText(ConfirmOrderActivity.this, "Checked " + radioBtnText, Toast.LENGTH_SHORT).show();
                paymentMethodDialog.dismiss();
            }
        });
        paymentMethodDialog.show();
    }

    private void checkUserStatus(){
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            //user is signed in
            //email = currentUser.getEmail();
            //uid = currentUser.getUid();
        }
        else {
            startActivity(new Intent(ConfirmOrderActivity.this, CredentialsActivity.class));
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
        startActivity(new Intent(ConfirmOrderActivity.this, HomeActivity.class));
    }
}