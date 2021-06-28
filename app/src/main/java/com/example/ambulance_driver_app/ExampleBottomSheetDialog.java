package com.example.ambulance_driver_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class ExampleBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    static String customerName = "Customer Name: "+MainActivity.custName;
    static String phone = "Phone No. " + MainActivity.custPhone;
    static String srcLatLng = "Source: ";
    static String destLatLng = "Dest: ";

    static TextView txtDriverName;
    static TextView txtDriverPhone;
    static TextView txtSrcLatLng;
    static TextView txtDestLatLng;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        Log.e("Dialog", "On Create View");
        Button button1 = v.findViewById(R.id.button1);
        Button button2 = v.findViewById(R.id.button2);
        txtDriverName = v.findViewById(R.id.customer_name);
        txtDriverPhone = v.findViewById(R.id.customer_phone);
        txtSrcLatLng = v.findViewById(R.id.src_lat_lng);
        txtDestLatLng = v.findViewById(R.id.dest_lat_lng);

        updateDetails();


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Button 1 clicked");
                String custPhone = MainActivity.custPhone;
                if(custPhone.equals("")) {
                    // should not call customer
                    Log.e("Here", "Should not call customer");
                }
                else {
                    // call the customer
                    Log.e("Here", "call the customer");
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                    callIntent.setData(Uri.parse("tel:"+custPhone));
                    callIntent.setData(Uri.parse("tel:7030584432"));
                    startActivity(callIntent);
                }
                dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("Button 2 clicked");
                dismiss();
            }
        });


        return v;
    }

    public static void updateDetails() {

        txtDriverName.setText(customerName);
        txtDriverPhone.setText(phone);
        txtSrcLatLng.setText(srcLatLng);
        txtDestLatLng.setText(destLatLng);
    }

    public static void resetDetails() {
        customerName = "Customer Name: ";
        phone = "Phone No. ";
        srcLatLng = "Source: ";
        destLatLng = "Dest: ";
        updateDetails();
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

}
