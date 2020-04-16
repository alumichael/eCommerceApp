package com.laundry.smartwash.UserMain_Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.NetworkConnection;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserPreferences;
import com.laundry.smartwash.WalletPaymentActivity;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_FundWallet extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.inputLayoutAmount)
    TextInputLayout inputLayoutAmount;
    @BindView(R.id.amount_editxt)
    EditText amountEditxt;
/*    @BindView(R.id.inputLayoutDescriptn)
    TextInputLayout inputLayoutDescriptn;
    @BindView(R.id.desc_editxt)
    EditText descEditxt;*/

   /* @BindView(R.id.inputLayoutPin)
    TextInputLayout inputLayoutPin;
    @BindView(R.id.pin_editxt)
    EditText pinEditText;

    @BindView(R.id.set_pin_txt)
    TextView set_pin_txt;

    @BindView(R.id.reset_pin_txt)
    TextView reset_pin_txt;*/

    @BindView(R.id.fund_btn)
    Button fundBtn;
    @BindView(R.id.fund_layout)
    CoordinatorLayout fund_layout;
    @BindView(R.id.avi1)
    AVLoadingIndicatorView avi1;
    UserPreferences userPreferences;
    Fragment fragment;

    public Fragment_FundWallet() {
        // Required empty public constructor
    }
    NetworkConnection networkConnection=new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

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
        View view=inflater.inflate(R.layout.fragment_fundwallet, container, false);
        ButterKnife.bind(this,view);
        userPreferences=new UserPreferences(getContext());

      /*  set_pin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userPreferences.getAgentPin().trim().length()!=4) {
                    pinAlert();
                }else{
                    showMessage("You have a pin");
                }
            }
        });*/

        fundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });


        return  view;
    }
   // private void sendData(String amount,String)

    private void Validate() {

        if (networkConnection.isNetworkConnected(getContext())) {
            boolean isValid = true;

            if (amountEditxt.getText().toString().isEmpty()) {
                inputLayoutAmount.setError("Amount is required!");
                isValid = false;
            } else {
                inputLayoutAmount.setErrorEnabled(false);

            }
/*else if (descEditxt.getText().toString().isEmpty()) {
                inputLayoutDescriptn.setError("Description is required!");
                isValid = false;
            }*/ /* else if (pinEditText.getText().toString().length() !=4 || !pinEditText.getText().toString().equals(userPreferences.getAgentPin())) {
                inputLayoutPin.setError("Incorrect pin entered!");
                isValid = false;
            }*/
            if (isValid) {

                //Do debit operation on Paystack and fund wallet API
                //Post Request to Api
                launchWalletPayment(amountEditxt.getText().toString());
                //sendData(amountEditxt.getText().toString(),descEditxt.getText().toString());
            }

            return;
        }
        showMessage("No Internet connection discovered!");
    }

    private void launchWalletPayment(String amount) {
        Intent intent = new Intent(getContext(), WalletPaymentActivity.class);
        intent.putExtra(Constant.WALLET_AMOUNT_FUNDING, amount);
        startActivity(intent);
        getActivity().finish();
    }

  /*  private void pinAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Wallet Pin");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_pass, null);
        builder.setView(dialogView);
        EditText oldPassword = dialogView.findViewById(R.id.oldpass);
        EditText newPassword = dialogView.findViewById(R.id.newpass);
        AVLoadingIndicatorView progressBar = dialogView.findViewById(R.id.progressbar);

        builder.setPositiveButton("Set Pin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (oldPassword.getText().toString().isEmpty() || oldPassword.getText().toString().trim().length() != 4) {
                    showMessage("Invalid pin! ensure it 4 digit");
                    return;
                } else if (newPassword.getText().toString().isEmpty() || newPassword.getText().toString().trim().length() != 4) {
                    showMessage("Invalid pin! ensure it 4 digit");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                UserPin userPin=new UserPin(newPassword.getText().toString().trim());
                setPin setPin=new setPin(userPin);

                //change_password(changePassPost);
                Call<ResponseBody> call = client.set_pin("Token " + userPreferences.getUserToken(), setPin);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.i("ResponseCode", String.valueOf(response.code()));
                        if(response.code()==406){
                            showMessage("Error! Wrong pin type provided!");
                            return;
                        }

                        if(response.code()==400){
                            showMessage("Check your internet connection");
                            return;

                        }else if(response.code()==429){
                            showMessage("Too many requests on database");
                            return;
                        }else if(response.code()==500){
                            showMessage("Server Error");
                            return;
                        }else if(response.code()==401){
                            showMessage("Unauthorized access, please try login again");
                            return;
                        }

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
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        userPreferences.setAgentPin(newPassword.getText().toString().trim());
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();

                        showMessage("Pin Successfully set");


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showMessage("Login Failed " + t.getMessage());
                        Log.i("GEtError", t.getMessage());
                        //progressBar.setVisibility(View.GONE);
                    }
                });


            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }*/



    private void showMessage(String s) {
        Snackbar.make(fund_layout, s, Snackbar.LENGTH_SHORT).show();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }



}
