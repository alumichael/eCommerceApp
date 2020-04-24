package com.laundry.smartwash.UserMain_Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.Banner.BannerGetData;
import com.laundry.smartwash.Model.Banner.BannerGetObj;
import com.laundry.smartwash.Model.Category.CategoryGetData;
import com.laundry.smartwash.Model.Category.CategoryGetObj;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.ServiceCard;
import com.laundry.smartwash.Model.Wallet.fetchWallet;
import com.laundry.smartwash.NetworkConnection;
import com.laundry.smartwash.R;
import com.laundry.smartwash.SignIn;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.adapter.CardAdapter;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Dashboard extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.dash_layout)
    FrameLayout dash_layout;

    @BindView(R.id.main_layout)
    ScrollView main_layout;

    @BindView(R.id.avi1)
    AVLoadingIndicatorView avi1;


    @BindView(R.id.wallet_balance)
    TextView wallet_balance;
    @BindView(R.id.wallet_kobo)
    TextView wallet_kobo;
    @BindView(R.id.fund_wallet_txt)
    TextView fund_wallet_txt;

    @BindView(R.id.fund_wallet_card)
    MaterialCardView fund_wallet_ServiceCard;
    @BindView(R.id.progressbar)
    AVLoadingIndicatorView progressbar;



    private CardAdapter cardAdapter;
    private List<CategoryGetData> ServiceList;
    private List<BannerGetData> bannerList;
    UserPreferences userPreferences;
    Fragment fragment;


    @BindView(R.id.slider)
    SliderLayout mSlider;
    @BindView(R.id.location_notify_btn)
    Button mLocationNotifyBtn;
    @BindView(R.id.location_note)
    TextView mLocationNote;

    @BindView(R.id.check_status)
    MaterialCardView mCheckStatus;

    @BindView(R.id.make_order)
    MaterialCardView mMakeOrder;

    @BindView(R.id.location_notify)
    MaterialCardView mLocationNotify;



    String balance;
    String username;



    //List<History> policy_item;

    public Fragment_Dashboard() {
        // Required empty public constructor
    }

    NetworkConnection networkConnection = new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Dashboard newInstance(String param1, String param2) {
        Fragment_Dashboard fragment = new Fragment_Dashboard();
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
        View view = inflater.inflate(R.layout.fragment_fragment__dashboard, container, false);
        ButterKnife.bind(this, view);
        userPreferences = new UserPreferences(getActivity());
        username=userPreferences.getFullName();
        balance=userPreferences.getWalletBalance();




        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
        nf.setMaximumFractionDigits(2);
        DecimalFormat df = (DecimalFormat) nf;
        String v_price = df.format(Double.valueOf(balance));


        wallet_balance.setText(v_price);
        //wallet_kobo.setVisibility(View.VISIBLE);

        String full_note = "Hi! " + username + ", SmartWash is currently operating near you.";
        mLocationNote.setText(full_note);
        
        //setWallet_balance();
        getServices();

        setAction();


        mLocationNotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new Fragment_Locations();
                showFragment(fragment);
            }
        });


        return view;
    }

    private void setAction() {
        mCheckStatus.setOnClickListener(this);
        fund_wallet_ServiceCard.setOnClickListener(this);
        mMakeOrder.setOnClickListener(this);
    }



    private void getServices() {
        avi1.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        if (networkConnection.isNetworkConnected(getContext())) {
            //get client and call object for request

            Call<CategoryGetObj> call = client.fetch_service_cat();
            call.enqueue(new Callback<CategoryGetObj>() {
                @Override
                public void onResponse(Call<CategoryGetObj> call, Response<CategoryGetObj> response) {

                    if (!response.isSuccessful()) {
                        try {
                            APIError apiError = ErrorUtils.parseError(response);

                            ErrorAlert("Fetch Failed: Check your network");
                            Log.i("Invalid Fetch", String.valueOf(apiError.getErrors()));
                            //Log.i("Invalid Entry", response.errorBody().toString());
                            avi1.setVisibility(View.GONE);
                            main_layout.setVisibility(View.GONE);

                        } catch (Exception e) {
                            Log.i("Fetch Failed", e.getMessage());
                            ErrorAlert("Fetch Failed: Check your network");
                            avi1.setVisibility(View.GONE);
                            main_layout.setVisibility(View.GONE);

                        }

                        return;
                    }

                    ServiceList = response.body().getData();


                    int count = ServiceList.size();


                    if (count == 0) {
                        ErrorAlert("Service unavailable for the moment");
                        avi1.setVisibility(View.GONE);
                        main_layout.setVisibility(View.GONE);

                    } else {

                        list();
                        getBanner();

                    }

                }


                @Override
                public void onFailure(Call<CategoryGetObj> call, Throwable t) {
                    ErrorAlert("Fetch Failed: Check your network");
                    Log.i("GEtError", t.getMessage());
                    avi1.setVisibility(View.GONE);
                    main_layout.setVisibility(View.GONE);
                }
            });

        }else{
            ErrorAlert("No Internet connection discovered!");
            avi1.setVisibility(View.GONE);
            main_layout.setVisibility(View.GONE);
        }


    }


    private void getBanner() {
        avi1.setVisibility(View.VISIBLE);
        main_layout.setVisibility(View.GONE);

        if (networkConnection.isNetworkConnected(getContext())) {
            //get client and call object for request

            Call<BannerGetObj> call = client.fetch_banner();
            call.enqueue(new Callback<BannerGetObj>() {
                @Override
                public void onResponse(Call<BannerGetObj> call, Response<BannerGetObj> response) {

                    if (!response.isSuccessful()) {
                        try {
                            APIError apiError = ErrorUtils.parseError(response);

                            ErrorAlert("Fetch Failed: Check your network");
                            Log.i("Invalid Fetch", String.valueOf(apiError.getErrors()));
                            //Log.i("Invalid Entry", response.errorBody().toString());
                            avi1.setVisibility(View.GONE);
                            main_layout.setVisibility(View.GONE);

                        } catch (Exception e) {
                            Log.i("Fetch Failed", e.getMessage());
                            ErrorAlert("Fetch Failed: Check your network");
                            avi1.setVisibility(View.GONE);
                            main_layout.setVisibility(View.GONE);

                        }

                        return;
                    }

                    bannerList = response.body().getData();


                    int count = bannerList.size();

                    if (count == 0) {

                        mSlider.setVisibility(View.GONE);

                    } else {

                        for (int i=0; i<count ;i++){
                            SLide(bannerList.get(i).getBannerUrl());
                        }


                    }

                }


                @Override
                public void onFailure(Call<BannerGetObj> call, Throwable t) {
                    ErrorAlert("Fetch Failed: Check your network");
                    Log.i("GEtError", t.getMessage());
                    avi1.setVisibility(View.GONE);
                    main_layout.setVisibility(View.GONE);
                }
            });

        }else{
            ErrorAlert("No Internet connection discovered!");
            avi1.setVisibility(View.GONE);
            main_layout.setVisibility(View.GONE);
        }


    }
    
    
    
    

    @SuppressLint("CheckResult")
    private void SLide(String banner_url){

        avi1.setVisibility(View.GONE);
        main_layout.setVisibility(View.VISIBLE);


        ArrayList<String> listUrl = new ArrayList<>();

        listUrl.add(banner_url);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_empty)
                .placeholder(R.drawable.img_empty);


        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getContext());
            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listUrl.get(i));
            mSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mSlider.setPresetTransformer(SliderLayout.Transformer.FlipHorizontal);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
        mSlider.addOnPageChangeListener(this);

    }


    private  void list(){

        cardAdapter = new CardAdapter(getContext(), ServiceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fund_wallet_card:
                fragment = new Fragment_FundWallet();
                showFragment(fragment);

                break;
            case R.id.check_status:
                fragment = new Fragment_Status();
                showFragment(fragment);
                break;

            case R.id.make_order:
                recyclerView.setVisibility(View.VISIBLE);
                showMessage("Check below for available services");
                break;
        }
    }


    private void showMessage(String s) {
        Snackbar.make(dash_layout, s, Snackbar.LENGTH_LONG).show();
    }


    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onStop() {
        mSlider.stopAutoCycle();
        super.onStop();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    private void ErrorAlert(String message) {

        new AlertDialog.Builder(getContext())
                .setTitle("Error ")
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setMessage(message)
                .setPositiveButton("Try-Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        getServices();

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



}
