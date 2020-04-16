package com.laundry.smartwash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    @BindView(R.id.img_logo)
    ImageView imgLogo;

    @BindView(R.id.txt_desc)
    TextView txtDesc;
  /*  @BindView(R.id.txt_version)
    TextView txtVersion;*/


    // Animation
    Animation slide_front_left, blink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//      Create instance of Preference class
        //UserPreferences userPreferences = new UserPreferences(this);

//        Display app version on the Screen
       /* try{
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = "v" + packageInfo.versionName;
            txtVersion.setText(version);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }*/

//        check for first time launch

            // load the animation
            slide_front_left = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_from_left);

            blink = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.blink);

            //start animation
            imgLogo.startAnimation(slide_front_left);

            txtDesc.startAnimation(blink);
            //txtVersion.startAnimation(blink);



            Thread myThread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(3000);

                        //Go to SignIN
                        startActivity(new Intent(getApplicationContext(),WelcomeSlideActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();

    }


}
