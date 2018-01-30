package com.example.vinayak2407.kisanmitra.registration;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.vinayak2407.kisanmitra.HomeScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.example.vinayak2407.kisanmitra.R;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener,BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private ProgressDialog pd;
    private Dialog dg;
    private Button resendButton;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextView timerView;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mSignInButton;
    private Button mVerifyButton;
    private HashMap<String,Integer> Hash_file_maps;
    private SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }



        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        //creating reference for verification button
        mVerifyButton=(Button)findViewById(R.id.button_start_verification);
        mVerifyButton.setOnClickListener(this);

        //initialising a progress dialog
        pd=new ProgressDialog(this);
        pd.setMessage("Please wait..");
        pd.setTitle("verifying");
        pd.setCanceledOnTouchOutside(false);


        //initialising the dialog
        dg=new Dialog(this);
        dg.setContentView(R.layout.activity_verify_dialog);
        dg.setCanceledOnTouchOutside(false);

        resendButton=dg.findViewById(R.id.button_resend);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(mPhoneNumberField.getText().toString(),mResendToken);
            }
        });
        //initialising editText in Dialog layout
        mVerificationField=(EditText)dg.findViewById(R.id.field_verification_code);
        //initialising signin button
        mSignInButton=(Button)dg.findViewById(R.id.button_verify);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Snackbar.make(view,"Please enter a valid OTP...",Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }
                mSignInButton.setEnabled(false);
                verifyPhoneNumberWithCode(mVerificationId, code);

            }
        });




        //---------------------Slider Layout code--------------------------

        Hash_file_maps=new HashMap<>();
        sliderLayout=(SliderLayout)findViewById(R.id.slider);
        Hash_file_maps.put("Kisan Mitra", R.drawable.a1);
        Hash_file_maps.put("Kisan Mitra1", R.drawable.a2);
        Hash_file_maps.put("Kisan Mitra2", R.drawable.a3);
        Hash_file_maps.put("Kisan Mitra3", R.drawable.a4);
        Hash_file_maps.put("Kisan Mitra4", R.drawable.a5);
        for(String name:Hash_file_maps.keySet()){
            TextSliderView textSliderView=new TextSliderView(PhoneAuthActivity.this);

            textSliderView.description(name)
                    .image(Hash_file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.addOnPageChangeListener(this);













        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                mSignInButton.setEnabled(true);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                // updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                mSignInButton.setVisibility(View.VISIBLE);
                resendButton.setVisibility(View.GONE);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
//                 [START_EXCLUDE]
//                 Update UI
//                updateUI(STATE_CODE_SENT);
//                 [END_EXCLUDE]
                timerView=dg.findViewById(R.id.mTimer);
                new CountDownTimer(45000,1000){

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long l) {
                        timerView.setText("00:"+String.valueOf(l/1000));
                    }

                    @Override
                    public void onFinish() {
                        timerView.setText("00:00");
                        mSignInButton.setVisibility(View.GONE);
                        resendButton.setVisibility(View.VISIBLE);

                    }
                }.start();

                Toast.makeText(getApplicationContext(),"Code sent",Toast.LENGTH_LONG).show();
                //dismissing the progress dialog
                pd.dismiss();
                dg.show();
            }
        };
        // [END phone_auth_callbacks]
    }




    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra")+"",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo","Page Changed: "+position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onStop(){
        sliderLayout.stopAutoCycle();
        super.onStop();
    }






    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();

                        if (task.isSuccessful()) {
                            dg.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            //  updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]


                            Toast.makeText(getApplicationContext(),"Verified successfully",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(PhoneAuthActivity.this,HomeScreenActivity.class));
                            finish();
                        } else {
                            mSignInButton.setEnabled(true);
                            Toast.makeText(getApplicationContext(),"Sign in failed\nPlease enter a valid OTP",Toast.LENGTH_LONG).show();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            // updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }

                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        //updateUI(STATE_INITIALIZED);
    }


    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_LONG).show();
                    return;

                }
                pd.show();
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;



            /*case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;

                }
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;

            case R.id.button_verify:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;*/
           /* case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;*/
        }
    }
}
