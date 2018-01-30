package com.example.vinayak2407.kisanmitra.registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinayak2407.kisanmitra.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();
        final Button signin=(Button)findViewById(R.id.button_signin);
        final Button signup=(Button)findViewById(R.id.button_signup);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewLogo);
        Animation fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        final TextView tv=(TextView)findViewById(R.id.textViewStart);
        fromtop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                signin.setVisibility(View.VISIBLE);
                signup.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.setAnimation(fromtop);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, PhoneAuthActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, AadharCardAcivity.class));
            }
        });


    }
}
