package com.codemountain.shoppadiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.fragments.HomeFragment;
import com.codemountain.shoppadiclient.model.ModelProducts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProductDetailsActivity";;
    private Toolbar toolbar;
    private ImageButton likesBtn, likesBtn2;
    private ImageView productDetailsImage;
    private TextView totalLikesText, productDetailsDesc, productDetailsName, productDetailsPrice, counter;
    private Button addToCartBtn, buyNowBtn;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference productRef, likesRef, cartRef;
    private boolean processLikes = false;
    private List<ModelProducts> detailsList = new ArrayList<>();

    //bottomDialog widgets
    private ImageView dialogImage;
    private TextView dialogName, dialogPrice, headerText;
    private ImageButton closeDialog;
    private NumberPicker dialogNumberPicker;
    private Button continueBtn;
    private ProgressBar progressBar;


    private int position, totalPrice, quantity, quantityChanged;
    private String name, desc, price, image, rating, categoryID, likes, uid;
    private boolean valueChanged = false;
    private String productFrom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mAuth = FirebaseAuth.getInstance();
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Product_Likes");
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");

        Intent intent = getIntent();
        position = intent.getIntExtra("POSITION", 0);
        productFrom = intent.getStringExtra("PRODUCT_FROM");

        if (productFrom != null && productFrom.equals("CategoryDetails")){
            detailsList = CategoryActivity.productList;
            Log.d(TAG, "onCreate: DETAILS LIST " + detailsList.size());
        }
        else {
            detailsList = HomeFragment.productList;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbarCartImageBtn = findViewById(R.id.toolbarCart);
        counter = findViewById(R.id.counter);
        productDetailsImage = findViewById(R.id.productDetailImage);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        buyNowBtn = findViewById(R.id.buyNowBtn);
        likesBtn = findViewById(R.id.likeBtn);
        likesBtn2 = findViewById(R.id.likeBtn2);
        totalLikesText = findViewById(R.id.totalLikes);
        productDetailsDesc = findViewById(R.id.productDetailDesc);
        productDetailsName = findViewById(R.id.productDetailName);
        productDetailsPrice = findViewById(R.id.productDetailsPrice);

        addToCartBtn.setOnClickListener(this);
        buyNowBtn.setOnClickListener(this);
        likesBtn.setOnClickListener(this);

        getProductDetails();
        setProductLikes();
    }

    private void getProductDetails() {
        name = detailsList.get(position).getName();
        desc = detailsList.get(position).getDescription();
        price = detailsList.get(position).getPrice();
        image = detailsList.get(position).getImage();
        rating = detailsList.get(position).getRating();
        likes = detailsList.get(position).getLikes();
        categoryID = detailsList.get(position).getCategoryID();

        productDetailsName.setText(name);
        productDetailsDesc.setText(desc);
        productDetailsPrice.setText(price);
        //productRatingBar.setRating(Float.parseFloat(rating));
        totalLikesText.setText(likes);
        Glide.with(ProductDetailsActivity.this)
                .load(image).placeholder(R.drawable.ic_launcher_background)
                .into(productDetailsImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addToCartBtn:
                setupDialogForCart();
                break;

            case R.id.buyNowBtn:
                setupDialogForBuyNow();
                break;

            case R.id.likeBtn:
                likeProduct();
                break;
        }
    }

    private void setupDialogForBuyNow() {
        final BottomSheetDialog buyNowDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheet = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                (LinearLayout)findViewById(R.id.bottomSheetContainer));

        dialogImage = bottomSheet.findViewById(R.id.dialogImage);
        dialogName = bottomSheet.findViewById(R.id.dialogName);
        headerText = bottomSheet.findViewById(R.id.headerText);
        dialogPrice = bottomSheet.findViewById(R.id.dialogPrice);
        dialogNumberPicker = bottomSheet.findViewById(R.id.dialogNumberPicker);
        closeDialog = bottomSheet.findViewById(R.id.closeDialogText);
        continueBtn = bottomSheet.findViewById(R.id.continueBtn);
        progressBar = bottomSheet.findViewById(R.id.dialogProgressBar);

        headerText.setText("Buy Now");
        dialogName.setText(name);
        dialogPrice.setText(price);
        Glide.with(this).load(image).placeholder(R.drawable.ic_launcher_background).into(dialogImage);
        quantity = dialogNumberPicker.getValue();

        dialogNumberPicker.setValueChangedListener((value, action) -> {
            int priceInt = Integer.parseInt(price);
            totalPrice = (priceInt * value);
            dialogPrice.setText(""+totalPrice);
            valueChanged = true;
        });

        closeDialog.setOnClickListener(v -> buyNowDialog.dismiss());

        continueBtn.setOnClickListener(v -> {
            String total = dialogPrice.getText().toString();
            startActivity(new Intent(ProductDetailsActivity.this, ConfirmOrderActivity.class)
                    .putExtra("TOTAL", total)
                    .putExtra("NAME", name)
                    .putExtra("QUANTITY", quantity)
                    .putExtra("FROM", "buyNowBtn"));
            buyNowDialog.dismiss();
        });

        buyNowDialog.setContentView(bottomSheet);
        buyNowDialog.show();
    }

    private void setupDialogForCart() {
        final BottomSheetDialog addToCartDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheet = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout,
                (LinearLayout)findViewById(R.id.bottomSheetContainer));

        dialogImage = bottomSheet.findViewById(R.id.dialogImage);
        dialogName = bottomSheet.findViewById(R.id.dialogName);
        headerText = bottomSheet.findViewById(R.id.headerText);
        dialogPrice = bottomSheet.findViewById(R.id.dialogPrice);
        dialogNumberPicker = bottomSheet.findViewById(R.id.dialogNumberPicker);
        closeDialog = bottomSheet.findViewById(R.id.closeDialogText);
        continueBtn = bottomSheet.findViewById(R.id.continueBtn);
        progressBar = bottomSheet.findViewById(R.id.dialogProgressBar);

        dialogName.setText(name);
        dialogPrice.setText(price);
        Glide.with(this).load(image).placeholder(R.drawable.ic_launcher_background).into(dialogImage);
        quantity = dialogNumberPicker.getValue();

        dialogNumberPicker.setValueChangedListener((value, action) -> {
            int priceInt = Integer.parseInt(price);
            totalPrice = (priceInt * value);
            dialogPrice.setText(""+totalPrice);
            quantityChanged = value;
            valueChanged = true;
        });

        closeDialog.setOnClickListener(v -> addToCartDialog.dismiss());

        continueBtn.setOnClickListener(v -> addProductToCart(addToCartDialog));

        addToCartDialog.setContentView(bottomSheet);
        addToCartDialog.show();
    }

    private void addProductToCart(final BottomSheetDialog bottomSheetDialog) {
        progressBar.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss: a");
        String currentTime = timeFormat.format(calendar.getTime());
        String cartID = currentDate+"_"+currentTime;

        HashMap<String, Object> cartHashMap = new HashMap<>();
        cartHashMap.put("date", currentDate);
        cartHashMap.put("time", currentTime);
        cartHashMap.put("discount", "0");
        cartHashMap.put("productID", name);
        //cartHashMap.put("cartID", cartID);
        if (valueChanged){
            cartHashMap.put("price", String.valueOf(totalPrice));
            cartHashMap.put("quantity", String.valueOf(quantityChanged));
        }
        else {
            cartHashMap.put("price", String.valueOf(price));
            cartHashMap.put("quantity", String.valueOf(quantity));
        }

        cartRef.child(uid).child("Items").child(name).updateChildren(cartHashMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProductDetailsActivity.this, "Product Added to Cart", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            //counter.setVisibility(View.VISIBLE);
            bottomSheetDialog.dismiss();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                bottomSheetDialog.dismiss();
                Toast.makeText(ProductDetailsActivity.this, "Failed to Add Product to Cart "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void likeProduct() {
        /**
         * create a node called productLikes inside create a child for each product
         * inside each product child create the user that liked child the set the value to liked
         * after that increase the likes count in the product node of that product and do vice-versa
         * if the user unlikes the product.
         */

        final int pLikes = Integer.parseInt(HomeFragment.productList.get(position).getLikes());
        processLikes = true;
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (processLikes){
                    if (dataSnapshot.child(name).hasChild(uid)){
                        //post already liked, so remove like
                        productRef.child(name).child("likes").setValue(""+ (pLikes - 1));
                        likesRef.child(name).child(uid).removeValue();
                        processLikes = false;
                    }
                    else {
                        //post not liked yet, like it
                        productRef.child(name).child("likes").setValue("" + (pLikes + 1));
                        likesRef.child(name).child(uid).setValue("Liked");
                        processLikes = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProductLikes() {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(name).hasChild(uid)){
                    //The current user has liked this product already
                    likesBtn.setVisibility(View.GONE);
                    likesBtn2.setVisibility(View.VISIBLE);
                }
                else {
                    //The currently signed user has not liked
                    likesBtn2.setVisibility(View.GONE);
                    likesBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(ProductDetailsActivity.this, HomeActivity.class));
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
            startActivity(new Intent(ProductDetailsActivity.this, CredentialsActivity.class));
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
        checkUserStatus();
        super.onResume();
    }
}
