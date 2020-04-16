package com.laundry.smartwash;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.laundry.smartwash.UserMain_Fragment.Fragment_Dashboard;
import com.laundry.smartwash.UserMain_Fragment.Fragment_Locations;
import com.laundry.smartwash.UserMain_Fragment.Fragment_Transactions;
import com.laundry.smartwash.UserMain_Fragment.ProfileFragment;
import com.laundry.smartwash.retrofit_interface.ApiInterface;
import com.laundry.smartwash.retrofit_interface.ServiceGenerator;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolBar;
    @BindView(R.id.main_content)
    LinearLayout main_content;


   /* @BindView(R.id.message)
    TextView mTextMessage;*/

    PermissionCheckClass mPermissionCheckClass;
    Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    applyToolbar("Dashboard", "We make it clean.");
                    fragment = new Fragment_Dashboard();
                    showFragment(fragment);
                    return true;
                case R.id.navigation_locations:
                    // mTextMessage.setText(R.string.title_customer);
                    applyToolbarChildren("Locations", "Available service locations");
                    fragment = new Fragment_Locations();
                    showFragment(fragment);

                    return true;
                case R.id.navigation_transaction:
                    // mTextMessage.setText(R.string.title_transaction);
                    applyToolbarChildren("Transactions", "Transactions history");
                    fragment = new Fragment_Transactions();
                    showFragment(fragment);
                    return true;

                case R.id.navigation_profile:
                    // mTextMessage.setText(R.string.title_profile);
                    applyToolbarChildren("Profile", "user information");
                    fragment = new ProfileFragment();
                    showFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        ButterKnife.bind(this);
        userPreferences = new UserPreferences(this);

        mPermissionCheckClass = new PermissionCheckClass(this);
        if (!mPermissionCheckClass.checkPermission()){
            mPermissionCheckClass.requestPermission();
        }

        applyToolbar("Dashboard", "We make it clean.");


        fragment = new Fragment_Dashboard();
        showFragment(fragment);
    }


    private void applyToolbar(String title, String subtitle) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
        getSupportActionBar().setElevation(0);

    }

    private void applyToolbarChildren(String title, String subtitle) {
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
        getSupportActionBar().setElevation(0);

       // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);

        //setting Elevation for > API 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolBar.setElevation(10f);
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_about) {

                startActivity(new Intent(MainActivity.this,AboutActivity.class));

            return true;
        } else if (itemId == R.id.refresh) {

            fragment = new Fragment_Dashboard();
            showFragment(fragment);


            return true;
        }else if (itemId == R.id.profile) {

            applyToolbarChildren("Profile", "user information");
            fragment = new ProfileFragment();
            showFragment(fragment);
            return true;

        }/* else if (itemId == R.id.action_change_pass) {

          //  changePassword();
            return true;

        } else if (itemId == R.id.action_faq) {

            return true;
        }*/ else if (itemId == R.id.action_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "SmartWash Mobile");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "We make it clean!");

            startActivity(Intent.createChooser(sharingIntent, "Share via"));


            return true;
        } else if (itemId == R.id.action_update) {

            goPlayStore();

            return true;
        }


       /* else if (itemId == R.id.action_email_us) {
            applyToolbarChildren("Email Us", "keep us informed");
            fragment = new EmailUsFragment();
            showFragment(fragment);
            return true;
        }*/else if (itemId == R.id.action_logout) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/"));
        startActivity(intent);
    }

    /*private void changePassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_pass, null);
        builder.setView(dialogView);
        EditText oldPassword = dialogView.findViewById(R.id.oldpass);
        EditText newPassword = dialogView.findViewById(R.id.newpass);
        AVLoadingIndicatorView progressBar = dialogView.findViewById(R.id.progressbar);

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (oldPassword.getText().toString().isEmpty() || oldPassword.getText().toString().trim().length() < 6) {
                    showMessage("Invalid Password, ensure at least 6 characters");
                    return;
                } else if (oldPassword.getText().toString().isEmpty() || oldPassword.getText().toString().trim().length() < 6) {
                    showMessage("Invalid Password, ensure at least 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                UserPassword userPassword = new UserPassword(oldPassword.getText().toString().trim(), newPassword.getText().toString().trim());

                ChangePassPost changePassPost = new ChangePassPost(userPassword);

                //change_password(changePassPost);
                Call<ResponseBody> call = client.change_password("Token " + userPreferences.getUserToken(), changePassPost);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                        showMessage("Password Changed Successfully");
                        startActivity(new Intent(MainActivity.this, SignIn.class));
                        finish();

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
    }
*/
    private void showMessage(String s) {
        Snackbar.make(main_content, s, Snackbar.LENGTH_LONG).show();
    }



    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
