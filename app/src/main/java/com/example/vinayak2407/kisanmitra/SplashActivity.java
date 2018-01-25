package com.example.vinayak2407.kisanmitra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.vinayak2407.kisanmitra.registration.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    ImageView imageViewtext;
    ImageView imageView;
    RelativeLayout linearLayout;

    Animation frombottom,fromtop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        imageView = (ImageView) findViewById(R.id.imageView2);
        fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        imageView.setAnimation(fromtop);

        linearLayout = (RelativeLayout) findViewById(R.id.splashlayout);

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {


        } else {

            Snackbar snackbar = Snackbar.make(linearLayout,"Connect to Internet",Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            finish();
        }else {

            new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity

                    Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }





    }



    @Override
    protected void onStart() {
        super.onStart();
        //Initialise FirebaseAuth

    }
}

