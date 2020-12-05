package com.codemountain.shoppadiclient.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.activity.HomeActivity;
import com.codemountain.shoppadiclient.activity.SettingsActivity;
import com.codemountain.shoppadiclient.adapters.OrdersAdapter;
import com.codemountain.shoppadiclient.model.ModelOrder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AccountFragment";
    private static final int REQUEST_CODE = 1;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ImageButton toolbarCartBtn, toolbarSettingsBtn;
    private CircleImageView accountImage;
    private TextView accountName, accountEmail;
    private CardView accountOrderCard, accountWishListCard;
    private ProgressBar progressBar;
    private FloatingActionButton camFab;
    private Uri imageUri;
    private byte[] bitmapBytes;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private String uid;
    private LinearLayout placedOrderLayout;
    private TextView orderIDText, orderStateText, orderDate, noOrderText;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<ModelOrder> orderList = new ArrayList<>();


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Customers");
        reference.keepSynced(true);

        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);

        //toolbarCartBtn = view.findViewById(R.id.toolbarCart);
        toolbarSettingsBtn = view.findViewById(R.id.toolbarSettings);
        accountImage = view.findViewById(R.id.accountImage);
        accountEmail = view.findViewById(R.id.accountEmail);
        accountName = view.findViewById(R.id.accountName);
        accountOrderCard = view.findViewById(R.id.accountOrderCard);
        //accountWishListCard = view.findViewById(R.id.accountWishListCard);
        progressBar = view.findViewById(R.id.accountProgress);
        camFab = view.findViewById(R.id.camFab);
        placedOrderLayout = view.findViewById(R.id.placedOrderLayout);
        orderIDText = view.findViewById(R.id.orderID);
        orderStateText = view.findViewById(R.id.orderState);
        orderDate = view.findViewById(R.id.orderDate);
        noOrderText = view.findViewById(R.id.noOrderText);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        toolbarSettingsBtn.setOnClickListener(this);
        //toolbarCartBtn.setOnClickListener(this);
        accountImage.setOnClickListener(this);
        //accountOrderCard.setOnClickListener(this);
        //accountWishListCard.setOnClickListener(this);
        camFab.setOnClickListener(this);


        getAccountDetails();
        loadOrderDetails();
        return view;
    }

    private void loadOrderDetails() {
        //List<ModelOrder> orderList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelOrder modelOrder = ds.getValue(ModelOrder.class);
                        orderList.add(modelOrder);
                    }
                    if (!(orderList.size() == 0)) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noOrderText.setVisibility(View.GONE);
                        /*orderIDText.setText("OrderID: " + orderList.get(0).getOrder_ID());
                        orderStateText.setText("State: " + orderList.get(0).getState());
                        orderDate.setText("Date: " + orderList.get(0).getDate());*/
                        ordersAdapter = new OrdersAdapter(getActivity(), orderList);
                        ordersAdapter.setOnItemClickListener((view, position) -> Toast.makeText(getActivity(), "Clicked " + position, Toast.LENGTH_SHORT).show());
                        ordersAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(ordersAdapter);

                    }
                    else {
                        recyclerView.setVisibility(View.GONE);
                        noOrderText.setVisibility(View.VISIBLE);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void getAccountDetails() {
        progressBar.setVisibility(View.VISIBLE);
        Query query = reference.orderByChild("email").equalTo(currentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String image = ds.child("image").getValue().toString();
                    String name = ds.child("name").getValue().toString();
                    String email = ds.child("email").getValue().toString();

                    if (getActivity() == null){
                        return;
                    }
                    Glide.with(getActivity()).load(image)
                            .placeholder(R.drawable.account_image_placeholder).into(accountImage);
                    accountEmail.setText(email);
                    accountName.setText(name);

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), "Failed to get details \n"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction;
        FragmentManager manager;
        switch (v.getId()){
            case R.id.toolbarSettings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;

            case R.id.accountImage:
            case R.id.camFab:
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }
                else {
                    openImageGallery();
                }
                break;

            /*case R.id.accountOrderCard:
                Snackbar.make(v, "Feature coming soon!", BaseTransientBottomBar.LENGTH_SHORT).show();
                break;*/

           /* case R.id.accountWishListCard:
                Snackbar.make(v, "WishList coming soon!", BaseTransientBottomBar.LENGTH_SHORT).show();
                break;*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null){
            imageUri = data.getData();

            //Glide.with(this).load(imageUri).into(accountImage);

            //Compressing the Image for Gallery
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //Converting to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            bitmapBytes = byteArrayOutputStream.toByteArray();

            //Glide.with(this).load(bitmapBytes).into(accountImage);
            uploadAccountImage();

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadAccountImage() {
        progressBar.setVisibility(View.VISIBLE);
        String filePath = "Customer_Images/"+"account_image_"+uid;
        storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
        storageReference.putBytes(bitmapBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //Check if image is uploaded and received
                if(uriTask.isSuccessful()){

                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("image", downloadUri.toString());

                    reference.child(currentUser.getUid()).updateChildren(profileMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Some error occurred\n"+uriTask.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload image\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(getContext(), "Accept for required permission", Toast.LENGTH_SHORT).show();
            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {

            openImageGallery();
        }
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), REQUEST_CODE);
    }
}
