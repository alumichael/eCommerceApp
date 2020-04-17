package com.laundry.smartwash.UserMain_Fragment.service_activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.Constant;
import com.laundry.smartwash.MainActivity;
import com.laundry.smartwash.Model.ClothList.ClothGetData;
import com.laundry.smartwash.Model.ClothList.ClothGetObj;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.OrderPost.OrderData;
import com.laundry.smartwash.Model.OrderPost.OrderHead;
import com.laundry.smartwash.Model.OrderPost.OrderedCloths;
import com.laundry.smartwash.NetworkConnection;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.OrderActivity;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.ClothingAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class serviceForm extends AppCompatActivity implements View.OnClickListener,ClothingAdapter.MessageAdapterListener,
 SwipeRefreshLayout.OnRefreshListener {



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

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @BindView(R.id.checkout)
    Button mCheckoutBtn;


    String cate_name="";
    String cate_price="";
    String currency;
    OrderData orderData;

    int no_of_item;
    int price;
    int special;

    private ClothingAdapter clothingAdapter;
    private List<ClothGetData> clothList;
    UserPreferences userPreferences;

    private ArrayList<ClothGetData> liquidList;
    ClothGetData clothGetData;
    ClothGetObj clothGetObj;

    NetworkConnection networkConnection = new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_form);
        ButterKnife.bind(this);

        userPreferences=new UserPreferences(this);

        Intent intent = getIntent();
        cate_name=intent.getStringExtra(Constant.CATE_NAME);
        cate_price=intent.getStringExtra(Constant.CATE_PRICE);



        mServiceTxt.setText(cate_name);
        currency="NGN " .concat(cate_price) + " per Cloth";
        mPriceTxt.setText(currency);

        if(cate_name.equals("Smartwash Liquid Soup")){

            liquidList= new ArrayList<>();
            clothGetData=new ClothGetData();
            clothGetData.setClothName("Liquid Soap");
            clothGetData.setClothAmount("100");

            liquidList.add(clothGetData);


            clothGetObj=new ClothGetObj();
            clothGetObj.setData(liquidList);

            clothList=clothGetObj.getData();
            lt();

        }else{
            init();
        }



        applyToolbarChildren("Ordering");

        mCheckoutBtn.setOnClickListener(this);


    }

    private void init() {
        getCloths();

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void lt(){

        clothingAdapter = new ClothingAdapter(getApplicationContext(), clothList,this, cate_price,cate_name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(clothingAdapter);
    }


    private void applyToolbarChildren(String title) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);


    }


    private void getCloths() {
        if (networkConnection.isNetworkConnected(this)) {
        
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
                        mSwipeRefreshLayout.setRefreshing(false);

                    } catch (Exception e) {
                        Log.i("Fetch Failed", e.getMessage());
                        showMessage("Fetch Failed");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    return;
                }

                mSwipeRefreshLayout.setRefreshing(false);
                clothList = response.body().getData();

                int count = clothList.size();

                if (count == 0) {
                    showMessage(cate_name+" is unavailable for the moment");
                    mSwipeRefreshLayout.setVisibility(View.GONE);


                } else{
                    lt();
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                }
            }



            @Override
            public void onFailure(Call<ClothGetObj> call, Throwable t) {
                showMessage("Fetch failed, check your internet ");
                Log.i("GEtError", t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        }else{

            showMessage("No Internet connection discovered!");
            mSwipeRefreshLayout.setRefreshing(false);

        }


    }





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
        startActivity(new Intent(serviceForm.this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }


    @Override
    public void onClothClicked(int position) {

        toggleSelection(position);


    }

    private void toggleSelection(int position) {
        clothingAdapter.toggleSelection(position);
        int count = clothingAdapter.getSelectedItemCount();

        if (count == 0) {
            showMessage("Zero Selection");
        } else {
            showMessage(count+" Selection");
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkout:


                no_of_item=userPreferences.getTotalPicked();

                String summaryTemp="Total in Basket: "+no_of_item+"\n"+"Total Price: "+userPreferences.getTotalPrice();
                userPreferences.setCategory(cate_name);

                List<OrderedCloths> orders =
                        clothingAdapter.getSelectedItems();
                orderData=new OrderData(userPreferences.getCategory(),userPreferences.getTotalPrice(),"pending",
                        orders);


                if(cate_name.equals("Smartwash Liquid Soup")){
                    int litre_count= orders.get(0).getQuantity();
                    if(litre_count<10){
                        ErrorAlert("You have to order beyond 9 litres, if you are ordering only for liquid soap, otherwise order" +
                                " less than 10litres along side cloth order. Thank you!");
                    }else{
                        basketSummary(summaryTemp);
                    }

                }else{
                    basketSummary(summaryTemp);
                }




                break;

         /*   case R.id.add_more_service:



                break;*/


        }
    }

    private void basketSummary(String summary) {

        new AlertDialog.Builder(this)
                .setTitle("Basket Summary")
                .setIcon(R.drawable.ic_shopping_basket_black_24dp)
                .setMessage(summary)
                .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();

                        Intent intent = new Intent(serviceForm.this, OrderActivity.class);
                        intent.putExtra("category", orderData.getCategory());
                        intent.putExtra("total_price", String.valueOf(orderData.getTotalAmount()));
                        intent.putExtra("order_status",orderData.getStatus());
                        intent.putExtra("order_list", (Serializable) orderData.getOrder());
                        startActivity(intent);
                        finish();

                        userPreferences.setTotalPicked(0);
                        userPreferences.setCount(0);
                        userPreferences.setTotalPrice(0);
                        //startActivity(new Intent(serviceForm.this, OrderActivity.class));
                       // finish();

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();

                    }
                })
                .show();

    }

    private void ErrorAlert(String error_msg) {

        new AlertDialog.Builder(this)
                .setTitle("Invalid Order")
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setMessage(error_msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();


                    }
                })


                .show();

    }

    @Override
    public void onRefresh() {
        getCloths();

    }
}
