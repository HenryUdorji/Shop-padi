package com.codemountain.shoppadiclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codemountain.shoppadiclient.OnItemClickListener;
import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.model.ModelCartList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ViewCartListAdapter extends RecyclerView.Adapter<ViewCartListAdapter.ViewCartListViewHolder> {

    private Context context;
    private List<ModelCartList> cartList;
    private DatabaseReference cartRef, productRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String uid;
    private OnItemClickListener listener;
    public static int overAllTotalPrice = 0;
    private String image;
    //private TotalListener totalListener;
    int totalPrice = 0;

    public ViewCartListAdapter(Context context, List<ModelCartList> cartList) {
        this.context = context;
        this.cartList = cartList;
        cartRef = FirebaseDatabase.getInstance().getReference("Cart_List");
        productRef = FirebaseDatabase.getInstance().getReference("Products");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
    }

    @NonNull
    @Override
    public ViewCartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_cart_layout, parent, false);
        return new ViewCartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewCartListViewHolder holder, int position) {
        final String productID = cartList.get(position).getProductID();
        final String quantity = cartList.get(position).getQuantity();
        final String price = cartList.get(position).getPrice();

        holder.cartProductName.setText(productID);
        holder.cartProductPrice.setText(price);
        holder.cartNumberPicker.setText(String.valueOf(quantity));

        //Query to get product image for cart item
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                image = dataSnapshot.child("image").getValue().toString();

                Glide.with(context).load(image)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(holder.cartImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Unable to retrieve image"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //set the overAllTotal price for the cartList
        /*int priceOfOne = Integer.parseInt(price) * Integer.parseInt(quantity);
        overAllTotalPrice = overAllTotalPrice + priceOfOne;*/

        //Listener for updating the quantity of product
        /*holder.cartNumberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                int newPrice = (Integer.parseInt(price) * value);
                HashMap<String, Object> cartHashMap = new HashMap<>();
                cartHashMap.put("quantity", String.valueOf(value));
                cartHashMap.put("price", String.valueOf(newPrice));

                //set new price to price textView
                holder.cartProductPrice.setText(String.valueOf(newPrice));

                //set the overAllTotal price for the cartList
                int priceOfOne = Integer.parseInt(price) * Integer.parseInt(quantity);
                overAllTotalPrice = overAllTotalPrice + priceOfOne;

                //update cartList node with new price and quantity
                cartRef.child(uid).child("Items").child(productID).updateChildren(cartHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Price & Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }

    public String calculateTotalPrice(){
        int totalPrice = 0;
        String price;
        //String quantity = null;
        for (int i = 0; i < cartList.size(); i++){
            price = cartList.get(i).getPrice();
            totalPrice += Integer.parseInt(price);
            /*for (int j = 0; j < cartList.size();){
                price += cartList.get(i).getPrice();
                quantity += cartList.get(j).getQuantity();

                totalPrice = Integer.parseInt(price) * Integer.parseInt(quantity);
            }*/
        }
        return String.valueOf(totalPrice);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class ViewCartListViewHolder extends RecyclerView.ViewHolder{
        private ImageView cartImageView;
        private TextView cartProductName, cartProductPrice, cartNumberPicker;
        //private NumberPicker cartNumberPicker;

        public ViewCartListViewHolder(@NonNull View itemView) {
            super(itemView);
            cartImageView = itemView.findViewById(R.id.cartImage);
            cartProductName = itemView.findViewById(R.id.cartName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartNumberPicker = itemView.findViewById(R.id.cartNumberPicker);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                }
                else {
                    Toast.makeText(context, "Null position clicked", Toast.LENGTH_SHORT).show();
                }
                return false;
            });
        }
    }

    public void setOnItemClickedListener(OnItemClickListener listener){
        this.listener = listener;
    }

   /* public interface TotalListener{
        void calculateTotal(int total);
    }

    public void calcTotal(){
        totalListener.calculateTotal(to);
    }*/
}
