package com.example.vinayak2407.kisanmitra;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinayak2407.kisanmitra.barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class AadharCardAcivity extends AppCompatActivity {
    private TextView barcodeValue;
    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean flag=false;
    protected String uid,name,gender,yearOfBirth,careOf,house,street,location,postOffice,district,state,postCode,rawString,dateofbirth;
    private DatabaseReference mUserData;
    private ProgressDialog pd;
    private Button but;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private  BottomSheetDialogFragment bottomSheetDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadhar_card_acivity);
        flag=false;
        HomeScreenActivity.aaddharDetails=this.getSharedPreferences("com.example.vinayak2407.kisanmitra", Context.MODE_PRIVATE);
        HomeScreenActivity.aaddharDetails.edit().putString("xmlString","null").apply();
        //Initializing a bottom sheet
        bottomSheetDialogFragment= new AadharDetails();
        mUserData= FirebaseDatabase.getInstance().getReference("Users");
        pd=new ProgressDialog(this);
        pd.setTitle("Registering!");
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        but=(Button)findViewById(R.id.read_barcode);

    }


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            v.setVisibility(View.INVISIBLE);
            // launch barcode activity.
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            //Bundle bndlanimation= ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.fromtop,R.anim.jump_to_down).toBundle();
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!HomeScreenActivity.aaddharDetails.getString("xmlString","null").equals("null"))
        {
            //bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            processString(HomeScreenActivity.aaddharDetails.getString("xmlString","null"));

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




    @SuppressLint("SetTextI18n")
    protected void processString(String input) {
        pd.show();
        rawString = input;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            if (input.startsWith("</?")) {
                input = input.replaceFirst("</\\?", "<?");
            }
            // Replace <?xml...?"> with <?xml..."?>
            input = input.replaceFirst("^<\\?xml ([^>]+)\\?\">", "<?xml $1\"?>");
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            dom = null;
        }
        if (dom != null) {
            Node node = dom.getChildNodes().item(0);
            NamedNodeMap attributes = node.getAttributes();

            uid = getAttributeOrEmptyString(attributes, "uid");
            name = getAttributeOrEmptyString(attributes, "name");
            gender = getAttributeOrEmptyString(attributes, "gender");
            postCode = getAttributeOrEmptyString(attributes, "pc");
            district = getAttributeOrEmptyString(attributes, "dist");
            state=getAttributeOrEmptyString(attributes,"state");
            postOffice=getAttributeOrEmptyString(attributes,"po");
            yearOfBirth=getAttributeOrEmptyString(attributes,"yob");
            careOf=getAttributeOrEmptyString(attributes,"co");
            house=getAttributeOrEmptyString(attributes,"house");
            location=getAttributeOrEmptyString(attributes,"loc");
            street=getAttributeOrEmptyString(attributes,"street");
            dateofbirth=getAttributeOrEmptyString(attributes,"dob");
            HashMap<String,String> aadharMap=new HashMap<>();
            aadharMap.put("uid",uid);
            aadharMap.put("name",name);
            aadharMap.put("gender",gender);
            aadharMap.put("postcode",postCode);
            aadharMap.put("district",district);
            aadharMap.put("state",state);
            aadharMap.put("postOffice",postOffice);
            aadharMap.put("yearOfBirth",yearOfBirth);
            aadharMap.put("house",house);
            aadharMap.put("address",location);
            aadharMap.put("street",street);
            aadharMap.put("dateofbirth",dateofbirth);
            mUserData.child(uid).setValue(aadharMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Aadhar Card Scanned Sucessfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AadharCardAcivity.this,PhoneAuthActivity.class));
                        finish();


                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Try again",Toast.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                }
            });








        }
    }

    private String getAttributeOrEmptyString(NamedNodeMap attributes, String attributeName) {
        Node node = attributes.getNamedItem(attributeName);
        if (node != null) {
            return node.getTextContent();
        } else {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        but.setVisibility(View.VISIBLE);

    }
}