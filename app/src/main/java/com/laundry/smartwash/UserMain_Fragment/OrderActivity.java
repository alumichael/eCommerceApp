package com.laundry.smartwash.UserMain_Fragment;

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
import com.laundry.smartwash.Constant;
import com.laundry.smartwash.MainActivity;
import com.laundry.smartwash.Model.ClothList.ClothGetData;
import com.laundry.smartwash.Model.ClothList.ClothGetObj;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.NetworkConnection;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.ClothingAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {



    @BindView(R.id.serviceform_toolbar)
    Toolbar toolBar;
    @BindView(R.id.serviceLayout)
    LinearLayout serviceLayout;

    @BindView(R.id.service_txt)
    TextView mServiceTxt;

    @BindView(R.id.price_txt)
    TextView mPriceTxt;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    @BindView(R.id.avi1)
    AVLoadingIndicatorView progressbar;

 /*   @BindView(R.id.basket_count)
    TextView mBasketCount;

    @BindView(R.id.total_price)
    TextView mTotalPrice;*/

    @BindView(R.id.add_more_service)
    Button mAddMoreServiceBtn;

    @BindView(R.id.checkout)
    Button mCheckoutBtn;


    String cate_name="";
    String cate_price="";
    String currency;

    private ClothingAdapter clothingAdapter;
    private List<ClothGetData> clothList;
    UserPreferences userPreferences;


    NetworkConnection networkConnection = new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        userPreferences=new UserPreferences(this);



       // getCloths();

        applyToolbarChildren("Order for Pick-Up");

    }


    private void applyToolbarChildren(String title) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);


    }


   /* private void getCloths() {
        if (networkConnection.isNetworkConnected(this)) {
        recycler_view.setVisibility(View.INVISIBLE);
        progressbar.setVisibility(View.VISIBLE);

        //get client and call object for request
        Call<ClothGetObj> call = client.fetch_cloths();
        call.enqueue(new Callback<ClothGetObj>() {
            @Override
            public void onResponse(Call<ClothGetObj> call, Response<ClothGetObj> response) {

                if (!response.isSuccessful()) {
                    try {
                        APIError apiError = ErrorUtils.parseError(response);

                        showMessage("Fetch Failed: " + apiError.getErrors());
                        Log.i("Invalid Fetch", String.valueOf(apiError.getErrors()));

                        //Log.i("Invalid Entry", response.errorBody().toString());

                        recycler_view.setVisibility(View.INVISIBLE);
                        progressbar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Log.i("Fetch Failed", e.getMessage());
                        showMessage("Fetch Failed");
                        recycler_view.setVisibility(View.INVISIBLE);
                        progressbar.setVisibility(View.GONE);

                    }

                    return;
                }

                clothList = response.body().getData();


                int count = clothList.size();


                if (count == 0) {
                    showMessage(cate_name+" is unavailable for the moment");
                    recycler_view.setVisibility(View.INVISIBLE);
                    progressbar.setVisibility(View.GONE);

                } else {

                    clothingAdapter = new ClothingAdapter(getApplicationContext(), clothList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
                    recycler_view.setLayoutManager(linearLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(clothingAdapter);

                    recycler_view.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);
                }
            }



            @Override
            public void onFailure(Call<ClothGetObj> call, Throwable t) {
                showMessage("Fetch failed, check your internet " + t.getMessage());
                Log.i("GEtError", t.getMessage());
            }
        });

        }else{

            showMessage("No Internet connection discovered!");
            recycler_view.setVisibility(View.INVISIBLE);
            progressbar.setVisibility(View.GONE);

        }


    }
*/




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    private void showMessage(String s) {
        Snackbar.make(serviceLayout, s, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderActivity.this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }



/*
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkout:
                int no_of_item=userPreferences.getTotalPicked();
                int price=no_of_item * Integer.parseInt(cate_price);

                String summary="Total in Basket: "+no_of_item+"\n"+"Total Price: "+price;

                basketSummary(summary);

                break;

            case R.id.add_more_service:



                break;





        }
    }*/
/*
    private void basketSummary(String summary) {

        new AlertDialog.Builder(this)
                .setTitle("Basket Summary")
                .setIcon(R.drawable.ic_shopping_basket_black_24dp)
                .setMessage(summary)
                .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        userPreferences.setTotalPicked(0);
                        userPreferences.setCount(0);

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();

                    }
                })
                .show();

    }*/
}
