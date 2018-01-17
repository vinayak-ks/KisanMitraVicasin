package com.example.vinayak2407.kisanmitra;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.vinayak2407.kisanmitra.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;



public class AadharCardAcivity extends AppCompatActivity {
    private TextView barcodeValue;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean flag=false;


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private  BottomSheetDialogFragment bottomSheetDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadhar_card_acivity);
        flag=false;
        HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
        //Initializing a bottom sheet
        bottomSheetDialogFragment= new AadharDetails();










    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            Bundle bndlanimation= ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.fromtop,R.anim.jump_to_down).toBundle();
            startActivityForResult(intent, RC_BARCODE_CAPTURE,bndlanimation);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!HomeScreenActivity.aaddharDetails.getString("xmlString","null").equals("null"))
        {
            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //statusMessage.setText(R.string.barcode_success);
                    //barcodeValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);

                    HomeScreenActivity.aaddharDetails.edit().putString("xmlString",barcode.displayValue).apply();


                } else {
                    //statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                    HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
                }
            } else {
                HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
                //statusMessage.setText(String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
            HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
        }
    }



}