package com.codemountain.shoppadiclient.utils;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codemountain.shoppadiclient.R;
import com.codemountain.shoppadiclient.activity.HomeActivity;
import com.codemountain.shoppadiclient.fragments.AccountFragment;
import com.codemountain.shoppadiclient.fragments.CartFragment;
import com.codemountain.shoppadiclient.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    public static void enableNavigation(final Context context, BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager;
                FragmentTransaction transaction;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        //context.startActivity(new Intent(context, HomeActivity.class));
                        //((HomeActivity)context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        HomeFragment homeFragment= new HomeFragment();
                        manager = ((HomeActivity)context).getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, homeFragment, "HOME");
                        transaction.commit();
                        return true;

                    case R.id.nav_cart:
                        //context.startActivity(new Intent(context, CartActivity.class));
                        //((HomeActivity)context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        CartFragment cartFragment = new CartFragment();
                        manager = ((HomeActivity)context).getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, cartFragment, "CART");
                        transaction.commit();
                        return true;

                    case R.id.nav_account:
                        //context.startActivity(new Intent(context, AccountActivity.class));
                        //((CartActivity)context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        AccountFragment accountFragment = new AccountFragment();
                        manager = ((HomeActivity)context).getSupportFragmentManager();
                        transaction = manager.beginTransaction();
                        transaction.replace(R.id.container, accountFragment, "ACCOUNT");
                        transaction.commit();
                        return true;
                }
                return false;
            }
        });
    }
}
