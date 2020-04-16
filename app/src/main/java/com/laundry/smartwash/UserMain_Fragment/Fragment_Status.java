package com.laundry.smartwash.UserMain_Fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.OrderStatusGet.OrderList;
import com.laundry.smartwash.Model.OrderStatusGet.OrderStatusData;
import com.laundry.smartwash.Model.OrderStatusGet.OrderStatusHead;
import com.laundry.smartwash.Model.Transaction.transactData;
import com.laundry.smartwash.Model.Transaction.transactHead;

import com.laundry.smartwash.R;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.OrderAdapter;
import com.laundry.smartwash.adapter.StatusAdapter;
import com.laundry.smartwash.adapter.TrasactionAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Status extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /** ButterKnife Code **/
    @BindView(R.id.status_layout)
    LinearLayout mStatusLayout;

    @BindView(R.id.not_found_layout)
    LinearLayout notFoundLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_status)
    RecyclerView mRecyclerStatus;
    /*@BindView(R.id.order_spinner)
    Spinner mOrderSPinner;*/
    /** ButterKnife Code **/

    //private TransactionAdapter transactionAdapter;
    LinearLayoutManager layoutManager;
    UserPreferences userPreferences;

    StatusAdapter statusAdapter;

    List<OrderStatusData> status_item;
    List<OrderList> order_item;


    ApiInterface client= ServiceGenerator.createService(ApiInterface.class);

    public Fragment_Status() {
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
        View view=inflater.inflate(R.layout.fragment_status, container, false);
        ButterKnife.bind(this,view);
        init();


        return  view;
    }

    private void init() {
        userPreferences = new UserPreferences(getContext());
        getOrderStatus();

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }



/*
    private void orderSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.status_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mOrderSPinner.setAdapter(staticAdapter);

        mOrderSPinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String statusText = (String) parent.getItemAtPosition(position);
                switch (statusText){
                    case "All Order":
                        getOrderStatus();
                        break;
                    case "Pending Order":

                        break;
                    case "Delivered Order":

                        break;

                    case "Received Order":

                        break;

                    default:
                        break;
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mOrderSPinner.getItemAtPosition(0);



            }
        });

    }
*/



    @Override
    public void onRefresh() {
        getOrderStatus();
    }


    private void getOrderStatus(){

        //get client and call object for request
        Call<OrderStatusHead> call=client.fetch_order_status(new OnlyIDRequest(userPreferences.getCustomerId()));
        call.enqueue(new Callback<OrderStatusHead>() {
            @Override
            public void onResponse(Call<OrderStatusHead> call, Response<OrderStatusHead> response) {

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

                status_item=response.body().getData();

                int count=status_item.size();

                Log.i("Re-SuccessSize", String.valueOf(status_item.size()));

                if(count==0){
                    notFoundLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);

                }else {
                    notFoundLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);

                    layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerStatus.setLayoutManager(layoutManager);
                    statusAdapter = new StatusAdapter(getContext(),status_item);
                    mRecyclerStatus.setAdapter(statusAdapter);
                    statusAdapter.notifyDataSetChanged();

                    Log.i("Success", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<OrderStatusHead> call, Throwable t) {
                showMessage("Fetch failed, Please try again");
                Log.i("GEtError",t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


    }


   /* private void getOrderByStatus(String status){

        //get client and call object for request
        Call<OrderStatusHead> call=client.fetch_order_status(userPreferences.getCustomerId());
        call.enqueue(new Callback<OrderStatusHead>() {
            @Override
            public void onResponse(Call<OrderStatusHead> call, Response<OrderStatusHead> response) {

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

                status_item=response.body().getData();

                int count=status_item.size();

                Log.i("Re-SuccessSize", String.valueOf(status_item.size()));

                if(count==0){
                    notFoundLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setVisibility(View.GONE);

                }else {
                    notFoundLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);


                    layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerStatus.setLayoutManager(layoutManager);
                    statusAdapter = new StatusAdapter(getContext(),status_item);
                    mRecyclerStatus.setAdapter(statusAdapter);
                    statusAdapter.notifyDataSetChanged();

                    Log.i("Success", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<OrderStatusHead> call, Throwable t) {
                showMessage("Fetch failed, Please try again");
                Log.i("GEtError",t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


    }


*/

    private void showMessage(String s) {
        Snackbar.make(mStatusLayout, s, Snackbar.LENGTH_LONG).show();
    }


}
