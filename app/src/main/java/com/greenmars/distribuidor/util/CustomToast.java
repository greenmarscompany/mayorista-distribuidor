package com.greenmars.distribuidor.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greenmars.distribuidor.R;

public class CustomToast {

    // Custom Toast Method
    public void Show_Toast(Context context, View view, String error) {

        // Layout Inflater for inflating custom view
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the layout over view
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) view.findViewById(R.id.toast_root));

        // Get TextView id and set error
        TextView text = (TextView) layout.findViewById(R.id.toast_error);
        text.setText(error);

        Toast toast = new Toast(context);// Get Toast Context
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);// Set
        toast.setDuration(Toast.LENGTH_LONG);// Set Duration
        toast.setView(layout); // Set Custom View over toast

        toast.show();// Finally show toast
    }

    // Custom Toast Method
    public void showConfirm(Context context, View view, String confirm) {

        // Layout Inflater for inflating custom view
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the layout over view
        View layout = inflater.inflate(R.layout.custom_toast_success,
                (ViewGroup) view.findViewById(R.id.toast_root));

        // Get TextView id and set error
        TextView text = (TextView) layout.findViewById(R.id.toast_success);
        text.setText(confirm);

        Toast toast = new Toast(context);// Get Toast Context
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);// Set
        toast.setDuration(Toast.LENGTH_LONG);// Set Duration
        toast.setView(layout); // Set Custom View over toast

        toast.show();// Finally show toast
    }

}
