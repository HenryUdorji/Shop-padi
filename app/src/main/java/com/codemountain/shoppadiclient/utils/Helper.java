package com.codemountain.shoppadiclient.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static void InternetAlertDialog(Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle("Error Connecting");

        //Setting Dialog Message
        alertDialog.setMessage("No Internet Connection");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Ok",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
