package com.codemountain.shoppadiclient.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codemountain.shoppadiclient.OnItemClickListener;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.activity.ConfirmOrderActivity;
import com.codemountain.shoppadiclient.activity.HomeActivity;
import com.codemountain.shoppadiclient.model.ModelCartList;
import com.codemountain.shoppadiclient.utils.SharedPref;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private Toolbar toolbar;
    private ImageButton toolbarCartBtn;
    private RecyclerView recyclerView;
    private List<ModelCartList> cartLists = new ArrayList<>();
    private Button checkOutBtn;
    private TextView totalText;
    private DatabaseReference cartRef, ordersRef, productRef;
    private String uid, image;
    private ActionMode actionMode;
    int overAllTotalPrice = 0;
    private FirebaseRecyclerOptions<ModelCartList> options;
    private FirebaseRecyclerAdapter<ModelCartList, CartListViewHolder> adapter;
    private SharedPref sharedPref;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);

        sharedPref = new SharedPref(getActivity());
        uid = sharedPref.getUIDString();

        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart_List");
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        productRef = FirebaseDatabase.getInstance().getReference("Products");

        checkOutBtn = view.findViewById(R.id.checkOutBtn);
        totalText = view.findViewById(R.id.cartProductPriceTotal);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    private class CartListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView cartImageView;
        private TextView cartProductName, cartProductPrice, cartNumberPicker;
        private OnItemClickListener listener;
        //private NumberPicker cartNumberPicker;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            cartImageView = itemView.findViewById(R.id.cartImage);
            cartProductName = itemView.findViewById(R.id.cartName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartNumberPicker = itemView.findViewById(R.id.cartNumberPicker);
        }

        @Override
        public void onClick(View v) {
            listener.itemClick(v, getAdapterPosition());
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<ModelCartList>()
                .setQuery(cartRef.child(uid).child("Items"), ModelCartList.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ModelCartList, CartListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartListViewHolder holder, final int position, @NonNull final ModelCartList model) {
                final String productID = model.getProductID();
                final String quantity = model.getQuantity();
                final String price = model.getPrice();

                holder.cartProductName.setText(productID);
                holder.cartProductPrice.setText(price);
                holder.cartNumberPicker.setText(String.valueOf(quantity));

                //calculate the total price for cartList
                int priceOfOne = ((Integer.valueOf(price))) * Integer.valueOf(quantity);
                overAllTotalPrice = overAllTotalPrice + priceOfOne;
                totalText.setText(String.valueOf(overAllTotalPrice));

                checkOutBtn.setVisibility(View.VISIBLE);
                checkOutBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), ConfirmOrderActivity.class)
                        .putExtra("TOTAL", String.valueOf(overAllTotalPrice))));

                //Query to get product image for cart item
                productRef.child(productID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        image = dataSnapshot.child("image").getValue().toString();

                        if (getContext() == null){
                            return;
                        }
                        Glide.with(getContext()).load(image)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(holder.cartImageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Unable to retrieve image"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                holder.itemView.setOnClickListener(v -> {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Delete Cart Item");
                    alertDialog.setMessage("Are you sure you want to delete this item from your Cart");
                    alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //deleteCartItem(position);
                            cartRef.child(uid).child("Items").child(model.getProductID())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Cart Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                });
            }

            @NonNull
            @Override
            public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.single_cart_layout, parent, false);
                return new CartListViewHolder(view);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        // Start Listening
        if(adapter != null){
            adapter.startListening();
        }
    }

    private void checkOutItems() {
        Toast.makeText(getContext(), "Order Placed", Toast.LENGTH_SHORT).show();
    }

    private void showAlertDialog(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete Cart Item");
        alertDialog.setMessage("Are you sure you want to delete this item from your Cart");
        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCartItem(position);
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showContextMenu(final int position) {
        if (actionMode != null){
            return;
        }
        toolbar.setVisibility(View.GONE);
        actionMode = ((HomeActivity)getActivity()).startSupportActionMode(new androidx.appcompat.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
                String productID = cartLists.get(position).getProductID();
                mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
                mode.setTitle(productID);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.contextDeleteCartItem){
                    deleteCartItem(position);
                    mode.finish();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
                actionMode = null;
                toolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void deleteCartItem(int position) {
        String productID = cartLists.get(position).getProductID();
        Query query = cartRef.child(uid).child("Items").orderByChild("productID").equalTo(productID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Cart Item Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "failed to delete item", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if(adapter != null){
            adapter.stopListening();
        }
    }
}
