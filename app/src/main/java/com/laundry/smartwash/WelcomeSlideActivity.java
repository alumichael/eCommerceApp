package com.laundry.smartwash;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.laundry.smartwash.frament.Welcome1Fragment;
import com.laundry.smartwash.frament.WelcomeSlide2Fragment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WelcomeSlideActivity extends BaseActivity implements View.OnClickListener {

    private TextView[] dots;
    @BindView(R.id.layoutDots)
    LinearLayout dotsLayout;

    @BindView(R.id.signin_btn)
    FrameLayout signin_btn;
    @BindView(R.id.wk_color)
    LinearLayout wk_color;

    /*@BindView(R.id.description)
    TextView description;*/

    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;
    private UserPreferences userPreferences;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    ArrayList<String> description_txt=new ArrayList<>(Arrays.asList(
            "Enjoy storex app in online marketing",
            "Checkout Cart for any item of your choice",
            "We drop you item at your door step"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome_slide);
        ButterKnife.bind(this);
        userPreferences = new UserPreferences(this);
        layouts = new int[]{R.layout.fragment_welcome1, R.layout.fragment_welcome_slide2};
        addBottomDots(0);
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        myViewPagerAdapter.addFragment(new Welcome1Fragment());
        myViewPagerAdapter.addFragment(new WelcomeSlide2Fragment());

        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        //description.setText(description_txt.get(0));


        signin_btn.setOnClickListener(this);

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.signin_btn) {
            //Toast.makeText(this,"nice Work today", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), SignIn.class));
            finish();
        }
    }

    private void addBottomDots(int currentPage) {
        this.dots = new TextView[this.layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        this.dotsLayout.removeAllViews();
        int i = 0;
        while (true) {
            TextView[] textViewArr = this.dots;
            if (i >= textViewArr.length) {
                break;
            }
            textViewArr[i] = new TextView(this);
            this.dots[i].setText(Html.fromHtml("&#9673"));
            this.dots[i].setTextSize(35.0f);
            this.dots[i].setTextColor(colorsInactive[currentPage]);
            this.dotsLayout.addView(this.dots[i]);
            i++;
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);


        }
    }


    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if(position>0){
                wk_color.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }else{
                wk_color.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            }
           // description.setText(description_txt.get(position));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };





    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }



    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList();

        MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return (Fragment) this.mFragmentList.get(position);
        }

        public int getCount() {
            return this.mFragmentList.size();
        }

        void addFragment(Fragment fragment) {
            this.mFragmentList.add(fragment);
        }
    }



}
