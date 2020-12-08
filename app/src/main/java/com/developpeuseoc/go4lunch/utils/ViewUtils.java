package com.developpeuseoc.go4lunch.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.developpeuseoc.go4lunch.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class for the snackbar
 */
public class ViewUtils {

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

}
