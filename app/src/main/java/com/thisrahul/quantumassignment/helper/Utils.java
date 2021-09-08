package com.thisrahul.quantumassignment.helper;

import android.app.ProgressDialog;
import android.content.Context;

public class Utils {

    //method for progressDialog
    public static ProgressDialog progressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        return progressDialog;
    }
}
