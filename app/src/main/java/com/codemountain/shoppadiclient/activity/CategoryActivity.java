package com.codemountain.shoppadiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.adapters.CategoryListAdapter;
import com.codemountain.shoppadiclient.adapters.ViewProductAdapter;
import com.codemountain.shoppadiclient.model.ModelCategoryList;
import com.codemountain.shoppadiclient.model.ModelProducts;
import com.codemountain.shoppadiclient.utils.SharedPref;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private static final String TAG = "CategoryActivity";
    private RecyclerView categoryListRecyclerView, categoryDetailsRecyclerView;
    private CategoryListAdapter categoryListAdapter;
    private ViewProductAdapter productAdapter;
    private List<ModelCategoryList> categoriesList = new ArrayList<>();
    public static List<ModelProducts> productList = new ArrayList<>();
    private String categoryName;
    private SharedPref sharedPref;
    private Toolbar toolbar;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        sharedPref = new SharedPref(this);
        categoryName = sharedPref.getCategoryListString();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        categoryListRecyclerView = findViewById(R.id.categoryListRecyclerView);
        categoryListRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoryListRecyclerView.setLayoutManager(layoutManager);

        categoryDetailsRecyclerView = findViewById(R.id.categoryDetailsRecyclerView);
        categoryDetailsRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryActivity.this, 2);
        categoryDetailsRecyclerView.setLayoutManager(gridLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: UID " + uid);


        loadCategoryList();
        loadCategoryDetails();
    }

    /**
     * This class loads products with the same category
     * based on the category clicked from category List
     */
    private void loadCategoryDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        reference.keepSynced(true);
        Query query = reference.orderByChild("categoryID").equalTo(categoryName);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                    productList.add(modelProducts);
                }
                Log.d(TAG, "onDataChange: Size of Details list " + productList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

        productAdapter = new ViewProductAdapter(CategoryActivity.this, productList);
        productAdapter.setOnItemClickedListener((view, position) ->
                startActivity(new Intent(CategoryActivity.this, ProductDetailsActivity.class)
                        .putExtra("POSITION", position)
                        .putExtra("PRODUCT_FROM", "CategoryDetails"))
        );
        categoryDetailsRecyclerView.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    /**
     * Loads list of category from the SERVER
     */
    private void loadCategoryList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelCategoryList modelCategoryList = ds.getValue(ModelCategoryList.class);
                    categoriesList.add(modelCategoryList);
                }
                Log.d(TAG, "onDataChange: Size of List " + categoriesList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
        categoryListAdapter = new CategoryListAdapter(CategoryActivity.this, categoriesList);
        categoryListAdapter.setOnItemClickListener((view, position) -> {
            String value = categoryListAdapter.getItem(position).getName();
            sharedPref.putStringInPref(SharedPref.CATEGORY, value);
            Log.d(TAG, "onPostExecute: Value saved " + value);
            productList.clear();
            CategoryActivity.this.recreate();
        });
        categoryListRecyclerView.setAdapter(categoryListAdapter);
        categoryListAdapter.notifyDataSetChanged();
    }


    private void checkUserStatus(){
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            //user is signed in
            //email = currentUser.getEmail();
            uid = currentUser.getUid();
        }
        else {
            startActivity(new Intent(CategoryActivity.this, CredentialsActivity.class));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(CategoryActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
