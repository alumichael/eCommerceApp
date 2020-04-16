package com.laundry.smartwash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.laundry.smartwash.Model.Errors.APIError;
import com.laundry.smartwash.Model.Errors.ErrorUtils;
import com.laundry.smartwash.Model.Message;
import com.laundry.smartwash.Model.OnlyIDAmountRequest;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.Transaction.VerifyTransact;
import com.laundry.smartwash.Model.Transaction.newTransact;
import com.laundry.smartwash.Model.Wallet.fetchWallet;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletPaymentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @BindView(R.id.payment_layout)
    LinearLayout payment_layout;

    @BindView(R.id.inputLayoutCardNo)
    TextInputLayout inputLayoutCardNo;
    @BindView(R.id.inputLayoutCVV)
    TextInputLayout inputLayoutCVV;


    @BindView(R.id.edit_card_number)
    EditText cardNumberE;
    @BindView(R.id.mm_spinner)
    Spinner mmSpinner;
    @BindView(R.id.yy_spinner)
    Spinner yySpinner;
    @BindView(R.id.cvv)
    EditText cvvE;

    @BindView(R.id.pay_button)
    Button payButton;
    @BindView(R.id.progress)
    AVLoadingIndicatorView mProgress;


    private Charge charge;
    ProgressDialog dialog;


    private UserPreferences userPreferences;
    NetworkConnection networkConnection = new NetworkConnection();
    ApiInterface client = ServiceGenerator.createService(ApiInterface.class);

    @BindView(R.id.amount)
    TextView mAmount;


    String amount = "", desc = "";
    String provider_ref = "";
    String transact_ref="";
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_payment);
        ButterKnife.bind(this);

        PaystackSdk.initialize(getApplicationContext());

        userPreferences = new UserPreferences(this);
        applyToolbarChildren("Fund Wallet");


        Intent intent = getIntent();
        amount = intent.getStringExtra(Constant.WALLET_AMOUNT_FUNDING);
        //desc = intent.getStringExtra(Constant.WALLET_DESC);

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
        nf.setMaximumFractionDigits(2);
        DecimalFormat df = (DecimalFormat) nf;
        String v_price = df.format(Double.valueOf(amount));

        Random random=new Random();
        transact_ref= String.valueOf(random.nextInt());


        String amt = getString(R.string.naira_currency) + " " + v_price;
        //desc = getString(R.string.desc) + ": " + desc;
        mAmount.setText(amt);


        mm();
        yy();

        if (amount != null || !amount.equals("")) {
            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (networkConnection.isNetworkConnected(getBaseContext())) {
                        boolean isValid = true;

                        String expiryMonth = mmSpinner.getSelectedItem().toString();
                        String expiryYear = yySpinner.getSelectedItem().toString();

                        if (cardNumberE.getText().toString().isEmpty()) {
                            inputLayoutCardNo.setError("Card Number is required!");
                            isValid = false;
                        } else if (cardNumberE.getText().toString().length() < 15) {
                            inputLayoutCardNo.setError("Card Number must be 16 upward!");
                            isValid = false;
                        } else {
                            inputLayoutCardNo.setErrorEnabled(false);
                        }

                        if (cvvE.getText().toString().isEmpty()) {
                            inputLayoutCVV.setError("CVV is required!");
                            isValid = false;
                        } else {
                            inputLayoutCVV.setErrorEnabled(false);
                        }

                        if (expiryMonth.equals("MM") || expiryYear.equals("YY")) {
                            isValid = false;
                            showShortMsg("Kindly choose, the Month & Year of expiry");
                        }


                        if (isValid) {
                            payButton.setEnabled(false);
//                        payButton.setBackgroundColor(Color.RED);
                            charge = new Charge();
                            dialogMessage("Performing transaction... please wait!");

                            String cardNumber = cardNumberE.getText().toString();
                            String cvv = cvvE.getText().toString();// cvv of the test card
//                        String cardNumber = "5060666666666666666";
                            int expiryMonthInt = Integer.parseInt(expiryMonth); //any month in the future
                            int expiryYearInt = Integer.parseInt(expiryYear); // any year in the future.
//                        String cvv = "123";  // cvv of the test card


                            Card card = new Card(cardNumber, expiryMonthInt, expiryYearInt, cvv);
                            if (card.isValid()) {
                                charge.setCard(card);
                                int cost = (int) Math.round(Double.parseDouble(amount));

                                charge.setAmount(cost * 100);
                                charge.setEmail(userPreferences.getUserEmail());
                                charge.setReference(transact_ref);


                                PaystackSdk.chargeCard(WalletPaymentActivity.this, charge, new Paystack.TransactionCallback() {
                                    @Override
                                    public void onSuccess(Transaction transaction) {

                                        payButton.setEnabled(false);
                                        // This is called only after transaction is deemed successful.
                                        // Retrieve the transaction, and send its reference to your server
                                        // for verification.
                                        provider_ref = transaction.getReference();
                                        Log.i("provider_ref", provider_ref);
                                        showShortMsg("Transaction Successfully, Please hold on");

                                        payButton.setVisibility(View.GONE);
                                        mProgress.setVisibility(View.VISIBLE);
                                        sendData(provider_ref);


                                    }

                                    @Override
                                    public void beforeValidate(Transaction transaction) {
                                        payButton.setEnabled(false);
//                                    payButton.setBackgroundColor(Color.RED);
                                        dialogMessage("Validating...please wait!");
                                        // This is called only before requesting OTP.
                                        // Save reference so you may send to server. If
                                        // error occurs with OTP, you should still verify on server.

                                        showShortMsg("loading...");
                                    }

                                    @Override
                                    public void onError(Throwable error, Transaction transaction) {
                                        payButton.setEnabled(true);
                                        dismissDialog();
                                        //handle error here
                                        showShortMsg("Failed: " + error.getMessage());
                                    }

                                });
                                // charge card
                                try {
                                    charge.putCustomField("Charged From", "Android SDK");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                payButton.setEnabled(true);
                                showShortMsg("Invalid card!");
                                dismissDialog();
                            }

                        }
                    } else {
                        showShortMsg("Kindly, connect to the internet!");
                    }


                }
            });
        } else {
            showShortMsg("Payment Reference is Null");
        }

    }

    private void sendData(String transRef) {

        newTransact transact=new newTransact(userPreferences.getCustomerId(),transRef,
                "Credit Wallet", Integer.parseInt(amount));

        Call<Message> new_transact = client.create_transact(transact);

        new_transact.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> new_transact, Response<Message> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showShortMsg("Invalid Entry: " + apiError.getErrors());
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showShortMsg("Invalid Entry");

                    }

                    return;
                }

               status=response.body().getStatus();
                if(status.equals("success")){
                    verifyPayment(transRef);
                }else{
                    showShortMsg("Transaction Failed, Please contact us.");
                    payButton.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                }





            }

            @Override
            public void onFailure(Call<Message> new_transact, Throwable t) {
                showShortMsg("Login Failed " + t.getMessage());
                Log.i("GEtError", t.getMessage());
                payButton.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);

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
                            showShortMsg("Invalid Entry: " + apiError.getErrors());
                            Log.i("Invalid EntryK", apiError.getErrors().toString());
                            Log.i("Invalid Entry", response.errorBody().toString());

                        } catch (Exception e) {
                            Log.i("InvalidEntry", e.getMessage());
                            showShortMsg("Invalid Entry");

                        }

                        return;
                    }

                    status=response.body().getStatus();
                    if(status.equals("success")){
                        creditWallet();
                    }else{
                        showShortMsg("Transaction Failed, Please contact us.");
                        payButton.setVisibility(View.VISIBLE);
                        mProgress.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailure(Call<Message> verify_transact, Throwable t) {
                    showShortMsg("Login Failed " + t.getMessage());
                    Log.i("GEtError", t.getMessage());
                    payButton.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);

                }
            });
        }


    private  void creditWallet(){
        OnlyIDAmountRequest onlyIDAmountRequest=new OnlyIDAmountRequest(userPreferences.getCustomerId(), Integer.parseInt(amount));
        Call<Message> creditWallet = client.credit_wallet(onlyIDAmountRequest);
        creditWallet.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> creditWallet, Response<Message> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showShortMsg("Invalid Entry: " + apiError.getErrors());
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showShortMsg("Invalid Entry");

                    }

                    return;
                }
                try {

                    refreshWallet();


                } catch (Exception e) {
                    showShortMsg("Error occured !");
                }

            }

            @Override
            public void onFailure(Call<Message> creditWallet, Throwable t) {
                showShortMsg("Login Failed " + t.getMessage());
                Log.i("GEtError", t.getMessage());

            }
        });
    }


    private  void refreshWallet(){

        Call<fetchWallet> refreshWallet = client.fetch_wallet(new OnlyIDRequest(userPreferences.getCustomerId()));
        refreshWallet.enqueue(new Callback<fetchWallet>() {
            @Override
            public void onResponse(Call<fetchWallet> creditWallet, Response<fetchWallet> response) {
                if (!response.isSuccessful()) {

                    try {
                        APIError apiError = ErrorUtils.parseError(response);
                        showShortMsg("Invalid Entry: " + apiError.getErrors());
                        Log.i("Invalid EntryK", apiError.getErrors().toString());
                        Log.i("Invalid Entry", response.errorBody().toString());

                    } catch (Exception e) {
                        Log.i("InvalidEntry", e.getMessage());
                        showShortMsg("Invalid Entry");

                    }

                    return;
                }
                try {
                    String status = response.body().getStatus();
                    if(status.equals("success")) {

                        String wallet_balance = response.body().fetchGetData().getAmount();
                        userPreferences.setWalletBalance(wallet_balance);
                        gotoDashboard();


                    }else {
                        String message=response.body().getMessage();
                        showShortMsg(message);
                        payButton.setVisibility(View.VISIBLE);
                        mProgress.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    showShortMsg("Error occured !");
                }

            }

            @Override
            public void onFailure(Call<fetchWallet> creditWallet, Throwable t) {
                showShortMsg("Login Failed " + t.getMessage());
                Log.i("GEtError", t.getMessage());
                payButton.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);

            }
        });
    }






    private void applyToolbarChildren(String title) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        //setting Elevation for > API 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolBar.setElevation(10f);
        }

    }


    private void mm() {
        // MM Spinner
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> mmAdapter = ArrayAdapter
                .createFromResource(this, R.array.mm_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        mmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        mmSpinner.setAdapter(mmAdapter);

        mmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //String stringTextLevel = (String) parent.getItemAtPosition(position);
                //electedView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void yy() {
// YY Spinner
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> yyAdapter = ArrayAdapter
                .createFromResource(this, R.array.yy_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        yyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        yySpinner.setAdapter(yyAdapter);

        yySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //String stringTextLevel = (String) parent.getItemAtPosition(position);
                //electedView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    private void showShortMsg(String s) {
        Snackbar.make(payment_layout, s, Snackbar.LENGTH_LONG).show();
    }

    private void dialogMessage(String s) {
        dialog = new ProgressDialog(WalletPaymentActivity.this);
        dialog.setMessage("Performing transaction... please wait");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    private void gotoDashboard() {
        startActivity(new Intent(WalletPaymentActivity.this, MainActivity.class));
        this.finish();
        Toast.makeText(getApplicationContext(), "Finalized Transaction", Toast.LENGTH_LONG).show();


    }


    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(WalletPaymentActivity.this, MainActivity.class));
        this.finish();
        super.onBackPressed();
    }
}
