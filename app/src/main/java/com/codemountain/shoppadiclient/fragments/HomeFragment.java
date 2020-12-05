package com.codemountain.shoppadiclient.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.activity.CategoryActivity;
import com.codemountain.shoppadiclient.activity.HomeActivity;
import com.codemountain.shoppadiclient.activity.ProductDetailsActivity;
import com.codemountain.shoppadiclient.adapters.ViewProductAdapter;
import com.codemountain.shoppadiclient.adapters.ViewPromoPagerAdapter;
import com.codemountain.shoppadiclient.model.ModelProducts;
import com.codemountain.shoppadiclient.model.ModelViewPromo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private Toolbar toolbar;
    private EditText toolbarSearchEdit;
    private ImageView toolbarCartBtn;
    private List<ModelViewPromo> viewPromoList = new ArrayList<>();
    public static List<ModelProducts> productList = new ArrayList<>();
    private ViewPromoPagerAdapter adapter;
    private ViewProductAdapter productAdapter;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton viewCategoryFab, viewWishListFab;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);

        viewCategoryFab = view.findViewById(R.id.viewCategoryFab);
        viewWishListFab = view.findViewById(R.id.viewWishListFab);
        //toolbarCartBtn = view.findViewById(R.id.toolbarCart);
        toolbarSearchEdit = view.findViewById(R.id.toolbarSearchEdit);
        viewPager = view.findViewById(R.id.viewPromoPager);
        tabLayout = view.findViewById(R.id.promoTabIndicator);
        recyclerView = view.findViewById(R.id.productRecyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        viewWishListFab.setOnClickListener(v -> Toast.makeText(getActivity(), "Feature coming soon!!", Toast.LENGTH_SHORT).show());
        viewCategoryFab.setOnClickListener(v -> startActivity(new Intent(getActivity(), CategoryActivity.class)));

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    searchProduct(s.toString());
                }
                else {
                    getAllProducts();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    searchProduct(s.toString());
                }
                else {
                    getAllProducts();
                }
            }
        };
        toolbarSearchEdit.addTextChangedListener(textWatcher);

        getAllProducts();
        getPromoImages();
        return view;
    }

    private void searchProduct(String query) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelProducts viewProduct = ds.getValue(ModelProducts.class);

                    if (viewProduct.getName().toLowerCase().contains(query.toLowerCase())
                            || viewProduct.getDescription().toLowerCase().contains(query.toLowerCase())) {
                        productList.add(viewProduct);
                    }
                }
                productAdapter = new ViewProductAdapter(getActivity(), productList);
                productAdapter.setOnItemClickedListener((view, position) -> startActivity(new Intent(getContext(), ProductDetailsActivity.class)
                        .putExtra("POSITION", position)));
                recyclerView.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelProducts viewProduct = ds.getValue(ModelProducts.class);
                    productList.add(viewProduct);
                }
                productAdapter = new ViewProductAdapter(getActivity(), productList);
                productAdapter.setOnItemClickedListener((view, position) -> startActivity(new Intent(getContext(), ProductDetailsActivity.class)
                        .putExtra("POSITION", position)));
                recyclerView.setAdapter(productAdapter);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPromoImages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Promo_Deals");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewPromoList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelViewPromo viewPromo = ds.getValue(ModelViewPromo.class);
                    viewPromoList.add(viewPromo);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter = new ViewPromoPagerAdapter(getContext(), viewPromoList);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //setup timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new promoSlideTimer(), 2000, 4000);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private class promoSlideTimer extends TimerTask{
        @Override
        public void run() {
            if (getActivity() == null){
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < viewPromoList.size() - 1){
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: " + "Fragment Attached");
    }
}


