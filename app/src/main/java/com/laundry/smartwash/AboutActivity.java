package com.laundry.smartwash;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.Message;
import com.laundry.smartwash.Model.OnlyIDAmountRequest;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.OrderPost.OrderData;
import com.laundry.smartwash.Model.OrderPost.OrderHead;
import com.laundry.smartwash.Model.OrderPost.OrderedCloths;
import com.laundry.smartwash.Model.Wallet.fetchWallet;
import com.laundry.smartwash.adapter.OrderAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutActivity extends AppCompatActivity{


    @BindView(R.id.serviceform_toolbar)
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        applyToolbarChildren("About Us");




        
    }


    private void applyToolbarChildren(String title) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onBackPressed() {
        startActivity(new Intent(AboutActivity.this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }




}
