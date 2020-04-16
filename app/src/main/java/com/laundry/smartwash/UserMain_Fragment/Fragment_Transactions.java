package com.laundry.smartwash.UserMain_Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.MainActivity;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.Transaction.transactData;
import com.laundry.smartwash.Model.Transaction.transactHead;

import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.service_activities.serviceForm;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.TrasactionAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Transactions extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /** ButterKnife Code **/
    @BindView(R.id.transaction_history_layout)
    LinearLayout mPaymentHistoryLayout;

    @BindView(R.id.not_found_layout)
    LinearLayout notFoundLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_transaction)
    RecyclerView mRecyclerTransaction;
    /** ButterKnife Code **/

    //private TransactionAdapter transactionAdapter;
    LinearLayoutManager layoutManager;
    UserPreferences userPreferences;
    TrasactionAdapter transactionAdapter;

    ApiInterface client= ServiceGenerator.createService(ApiInterface.class);
    
    public Fragment_Transactions() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_Locations newInstance(String param1, String param2) {
        Fragment_Locations fragment = new Fragment_Locations();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_transactions, container, false);
        ButterKnife.bind(this,view);
        userPreferences = new UserPreferences(getContext());
        init();


        return  view;
    }

    private void init() {

        getTransactionHistory();

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getTransactionHistory();
    }

    private void getTransactionHistory(){
        OnlyIDRequest onlyIDRequest=new OnlyIDRequest(userPreferences.getCustomerId());

        Log.i("UserID", userPreferences.getCustomerId());
        //get client and call object for request
        Call<transactHead> call=client.fetch_transaction(onlyIDRequest);
        call.enqueue(new Callback<transactHead>() {
            @Override
            public void onResponse(Call<transactHead> call, Response<transactHead> response) {

                if(!response.isSuccessful()){
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

                List<transactData> item=response.body().getData();

                int count=item.size();

                Log.i("Re-SuccessSize", String.valueOf(item.size()));
                String status=response.body().getStatus();
                if(count==0 && status.equals("success")){
                    notFoundLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);

                }else if(count==0 && status.equals("failed")){
                    String message=response.body().getMessage();
                    ErrorAlert(message);
                } else{
                    notFoundLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerTransaction.setLayoutManager(layoutManager);
                    transactionAdapter = new TrasactionAdapter(getContext(),item);
                    mRecyclerTransaction.setAdapter(transactionAdapter);
                    transactionAdapter.notifyDataSetChanged();

                    Log.i("Success", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<transactHead> call, Throwable t) {
                showMessage("Fetch failed, Please try again");
                Log.i("GEtError",t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void showMessage(String s) {
        Snackbar.make(mPaymentHistoryLayout, s, Snackbar.LENGTH_SHORT).show();
    }

    private void ErrorAlert(String error_msg) {

        new AlertDialog.Builder(getContext())
                .setTitle("Fetch Failed")
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setMessage(error_msg)
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        getTransactionHistory();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                })
                .show();

    }


}
