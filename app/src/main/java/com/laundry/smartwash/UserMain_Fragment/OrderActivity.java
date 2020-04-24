package com.laundry.smartwash.UserMain_Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.laundry.smartwash.Model.Message;
import com.laundry.smartwash.Model.OnlyIDAmountRequest;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.OrderPost.OrderData;
import com.laundry.smartwash.Model.OrderPost.OrderHead;
import com.laundry.smartwash.Model.OrderPost.OrderedCloths;
import com.laundry.smartwash.Model.Transaction.VerifyTransact;
import com.laundry.smartwash.Model.Transaction.newTransact;
import com.laundry.smartwash.Model.Wallet.fetchWallet;
import com.laundry.smartwash.NetworkConnection;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.service_activities.serviceForm;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.ClothingAdapter;
import com.laundry.smartwash.adapter.OrderAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {



    @BindView(R.id.serviceform_toolbar)
    Toolbar toolBar;
    @BindView(R.id.orderLayout)
    LinearLayout orderLayout;

    @BindView(R.id.cate_name)
    TextView mCatName;

    @BindView(R.id.duration)
    TextView mDuration;

    @BindView(R.id.total_amount)
    TextView mTotalAmount;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.avi1)
    AVLoadingIndicatorView mProgressBar;

    @BindView(R.id.submit_button)
    Button mSubmtButton;

    String category="";
    int total_price=0;
    String order_status="";
    String transact_ref="";
    
    private OrderAdapter orderAdapter;
    private List<OrderedCloths> orderedCloths;
    UserPreferences userPreferences;


    NetworkConnection networkConnection = new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        applyToolbarChildren("Finalize Ordering");
        userPreferences=new UserPreferences(this);
        
        mProgressBar.setVisibility(View.GONE);
        mSubmtButton.setOnClickListener(this);

        Intent intent = getIntent();
        category=intent.getStringExtra("category");
        total_price= Integer.parseInt(intent.getStringExtra("total_price"));
        orderedCloths=(List<OrderedCloths>)intent.getExtras().getSerializable("order_list");
        order_status=intent.getStringExtra("order_status");

        Random random=new Random();
        transact_ref= String.valueOf(random.nextInt());



        
        mCatName.setText(category);
        mTotalAmount.setText(String.valueOf(total_price));

        if(category.equals("Executive Wash")){
            mDuration.setText("Wash Duration: 48hours");
        }else if(category.equals("Express Wash")){
            mDuration.setText("Wash Duration: 8hours");
        }else if(category.equals("Smartwash Liquid Soup")){
            mDuration.setText("Delivery Duration: 24hours");
        }else{
            mDuration.setText("Wash Duration: 72hours");
        }

        orderRecycler();
        
    }


    private void applyToolbarChildren(String title) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);


    }


    private void orderRecycler(){

        orderAdapter = new OrderAdapter(this, orderedCloths);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(orderAdapter);
    }


   


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onBackPressed() {
        startActivity(new Intent(OrderActivity.this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }

    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_button:

                DebitWallet();
                break;
                
        }
    }


    private void DebitWallet() {
        if (networkConnection.isNetworkConnected(this)) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSubmtButton.setVisibility(View.GONE);

            OnlyIDAmountRequest onlyIDAmountRequest=new OnlyIDAmountRequest(userPreferences.getCustomerId(),total_price);
            Call<Message> call = client.debit_wallet(onlyIDAmountRequest);

            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {

                    if (!response.isSuccessful()) {
                        try {
                            APIError apiError = ErrorUtils.parseError(response);

                            showMessage("Fetch Failed: " + apiError.getErrors());
                            Log.i("Invalid Fetch", String.valueOf(apiError.getErrors()));

                            //Log.i("Invalid Entry", response.errorBody().toString());

                            mRecyclerView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            mSubmtButton.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            Log.i("Fetch Failed", e.getMessage());
                            showMessage("Fetch Failed");
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            mSubmtButton.setVisibility(View.VISIBLE);

                        }

                        return;
                    }

                    String status = response.body().getStatus();
                    if(status.equals("success")){
                        submitOrder();
                    }else{

                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSubmtButton.setVisibility(View.VISIBLE);

                        String message=response.body().getMessage();
                        ErrorAlert(message);


                    }

                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    ErrorAlert("Failed to Order");
                    //showMessage("Debit failed, check your internet " + t.getMessage());
                    Log.i("GEtError", t.getMessage());
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mSubmtButton.setVisibility(View.VISIBLE);
                }
            });

        }else{

            ErrorAlert("No Internet connection discovered!");
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSubmtButton.setVisibility(View.VISIBLE);

        }


    }


    private void submitOrder() {

        OrderData orderData=new OrderData(category,total_price,order_status,
                orderedCloths);

        OrderHead orderHead=new OrderHead(userPreferences.getCustomerId(),orderData);

        if (networkConnection.isNetworkConnected(this)) {

        //get client and call object for request
        Call<Message> call = client.new_order(orderHead);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                if (!response.isSuccessful()) {
                    try {
                        APIError apiError = ErrorUtils.parseError(response);

                        showMessage("Fetch Failed: " + apiError.getErrors());
                        Log.i("Invalid Fetch", String.valueOf(apiError.getErrors()));

                        //Log.i("Invalid Entry", response.errorBody().toString());

                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSubmtButton.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Log.i("Fetch Failed", e.getMessage());
                        showMessage("Fetch Failed");
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSubmtButton.setVisibility(View.VISIBLE);

                    }

                    return;
                }


                String status = response.body().getStatus();
                if(status.equals("success")){

                    refreshWallet();

                }else{

                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mSubmtButton.setVisibility(View.VISIBLE);
                    String message=response.body().getMessage();
                    ErrorAlert(message);
                }
            }



            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                //showMessage("Fetch failed, check your internet " + t.getMessage());
                Log.i("GEtError", t.getMessage());
            }
        });

        }else{

            showMessage("No Internet connection discovered!");
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSubmtButton.setVisibility(View.VISIBLE);


        }


    }

    private void ErrorAlert(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Error ")
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setMessage(message)
                .setPositiveButton("Try-Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        DebitWallet();

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


    private void SuccessAlert(String message) {

        new AlertDialog.Builder(this)
                .setTitle("Success Alert")
                .setIcon(R.drawable.ic_done_all_black_24dp)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        startActivity(new Intent(OrderActivity.this, MainActivity.class));
                        finish();

                    }
                })

                .show();

    }

    private  void refreshWallet(){

        Call<fetchWallet> creditWallet = client.fetch_wallet(new OnlyIDRequest(userPreferences.getCustomerId()));
        creditWallet.enqueue(new Callback<fetchWallet>() {
            @Override
            public void onResponse(Call<fetchWallet> creditWallet, Response<fetchWallet> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showMessage("Invalid Entry: " + apiError.getErrors());
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSubmtButton.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showMessage("Invalid Entry");
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        mSubmtButton.setVisibility(View.VISIBLE);

                    }

                    return;
                }
                try {
                    String status = response.body().getStatus();
                    if(status.equals("success")) {


                        String wallet_balance = response.body().fetchGetData().getAmount();
                        userPreferences.setWalletBalance(wallet_balance);


                        newTransact(transact_ref,total_price);



                    }else {
                        String message=response.body().getMessage();
                        ErrorAlert(message);
                    }


                } catch (Exception e) {
                    showMessage("Error occured !");
                }

            }

            @Override
            public void onFailure(Call<fetchWallet> creditWallet, Throwable t) {
                showMessage("Error occured !");
                Log.i("GEtError", t.getMessage());
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mSubmtButton.setVisibility(View.VISIBLE);

            }
        });
    }

    private void newTransact(String transRef,int total_price) {
        newTransact transact=new newTransact(userPreferences.getCustomerId(),transRef,
                "Debit Wallet", total_price);

        Call<Message> new_transact = client.create_transact(transact);

        new_transact.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> new_transact, Response<Message> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showMessage("Failed to update transaction: ");
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showMessage("Invalid Entry");

                    }

                    return;
                }

                String status=response.body().getStatus();
                if(status.equals("success")){
                    verifyPayment(transRef);
                }else{
                    showMessage("Successful Order, but failed to update new transaction.");
                }

            }

            @Override
            public void onFailure(Call<Message> new_transact, Throwable t) {
                showMessage("Failed to update new transaction " + t.getMessage());
                Log.i("GEtError", t.getMessage());

            }
        });


    }

    private  void verifyPayment(String transref){
        VerifyTransact verifyTransact=new VerifyTransact(userPreferences.getCustomerId(), transref);
        Call<Message> verify_transact = client.verify_transact(verifyTransact);
        verify_transact.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> verify_transact, Response<Message> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showMessage("Invalid Entry: " + apiError.getErrors());
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showMessage("Invalid Entry");

                    }

                    return;
                }

                String status=response.body().getStatus();
                if(status.equals("success")){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mSubmtButton.setVisibility(View.VISIBLE);

                    String successMessage="Wash request is successfully, We will contact you soon !";
                    SuccessAlert(successMessage);


                }else{
                    showMessage("Failed to verify transaction.");
                }

            }

            @Override
            public void onFailure(Call<Message> verify_transact, Throwable t) {
                showMessage("Failed to verify transaction" + t.getMessage());
                Log.i("GEtError", t.getMessage());

            }
        });
    }




    private void showMessage(String s) {
        Snackbar.make(orderLayout, s, Snackbar.LENGTH_LONG).show();
    }

}
