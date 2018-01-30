package com.example.vinayak2407.kisanmitra.registration;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinayak2407.kisanmitra.HomeScreenActivity;
import com.example.vinayak2407.kisanmitra.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AadharDetails extends BottomSheetDialogFragment {
    protected String uid,name,gender,yearOfBirth,careOf,house,street,location,postOffice,district,state,postCode,rawString,dateofbirth;
    private AutoCompleteTextView aUid,aName,aGender,ahouse,astreet,adistrict,astate,apostCode,adateofbirth,aAddress;
    private Button submit;
    HashMap<String,String> aadharMap;
    private DatabaseReference mUserData;
    private ProgressDialog pd;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @SuppressLint("NewApi")
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
            if(newState==3){
                Log.i("scroll","yess");
                bottomSheet.setVerticalScrollBarEnabled(false);
            }
            Log.i("state", String.valueOf(newState));
        }


        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // React to dragging events

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.activity_aadhar_details, null);
        dialog.setContentView(contentView);


        TextView v=contentView.findViewById(R.id.barcode_value);
        aUid=contentView.findViewById(R.id.editTextAadharNumber);
        aName=contentView.findViewById(R.id.editTextName);
        aGender=contentView.findViewById(R.id.editTextGender);
        adateofbirth=contentView.findViewById(R.id.editTextDob);
        ahouse=contentView.findViewById(R.id.editTextHouse);
        adistrict=contentView.findViewById(R.id.editTextDistrict);
        apostCode=contentView.findViewById(R.id.editTextPinCode);
        astate=contentView.findViewById(R.id.editTextState);
        astreet=contentView.findViewById(R.id.editTextStreet);
        aAddress=contentView.findViewById(R.id.editTextAddress);
        submit=contentView.findViewById(R.id.submit);
        aadharMap=new HashMap<>();
        mUserData= FirebaseDatabase.getInstance().getReference("Users");
        pd=new ProgressDialog(contentView.getContext());
        pd.setTitle("Registering!");
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!uid.equals(""))
                {

                    pd.show();

                    uid=aUid.getText().toString();
                    name=aName.getText().toString();
                    gender=aGender.getText().toString();
                    postCode=apostCode.getText().toString();
                    state=astate.getText().toString();
                    district=adistrict.getText().toString();
                    house=ahouse.getText().toString();
                    street=astreet.getText().toString();
                    location=aAddress.getText().toString();
                    dateofbirth=adateofbirth.getText().toString();



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
                                Toast.makeText(contentView.getContext(),"Registered Sucessfully",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(contentView.getContext(),"Try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


        v.setText(HomeScreenActivity.aaddharDetails.getString("xmlString","no data found"));
        processString(HomeScreenActivity.aaddharDetails.getString("xmlString","no data found"));






        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }


    @SuppressLint("SetTextI18n")
    protected void processString(String input) {
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




            aUid.setText(uid);
            aName.setText(name);
            aGender.setText(gender);
            apostCode.setText(postCode);
            astate.setText(state);
            adistrict.setText(district);
            ahouse.setText(house);
            astreet.setText(street);
            aAddress.setText(careOf+", "+location);
            adateofbirth.setText(dateofbirth);







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





}
