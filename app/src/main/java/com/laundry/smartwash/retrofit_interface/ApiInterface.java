package com.laundry.smartwash.retrofit_interface;



import com.laundry.smartwash.Model.Banner.BannerGetObj;
import com.laundry.smartwash.Model.Category.CategoryGetObj;
import com.laundry.smartwash.Model.ClothList.ClothGetObj;
import com.laundry.smartwash.Model.LoginModel.UserGetObj;
import com.laundry.smartwash.Model.LoginModel.UserPostData;
import com.laundry.smartwash.Model.Message;
import com.laundry.smartwash.Model.OnlyIDAmountRequest;
import com.laundry.smartwash.Model.OnlyIDRequest;
import com.laundry.smartwash.Model.OrderPost.OrderHead;
import com.laundry.smartwash.Model.OrderStatusGet.OrderStatusHead;
import com.laundry.smartwash.Model.Profile.Profile_updatePost;
import com.laundry.smartwash.Model.Register.RegisterGetData;
import com.laundry.smartwash.Model.Register.RegisterGetObj;
import com.laundry.smartwash.Model.Register.RegisterUserPost;
import com.laundry.smartwash.Model.Transaction.VerifyTransact;
import com.laundry.smartwash.Model.Transaction.newTransact;
import com.laundry.smartwash.Model.Transaction.transactHead;
import com.laundry.smartwash.Model.Wallet.createWallet;
import com.laundry.smartwash.Model.Wallet.fetchWallet;
import com.laundry.smartwash.Model.fetchByStatus;
import com.laundry.smartwash.Model.updateOrder;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ApiInterface {

    @POST("api/auth/register.php")
    Call<Message> register(@Body RegisterUserPost regPostData);

    @POST("api/auth/login.php")
    Call<UserGetObj> login(@Body UserPostData userPostData);

    @POST("api/Auth/updateprofile.php")
    Call<ResponseBody> update_profile(@Body Profile_updatePost profile_updatePost);

    @POST("api/wallet/create.php")
    Call<ResponseBody> create_wallet(@Body createWallet createWallet);

    @GET("api/category/fetchCategories.php")
    Call<CategoryGetObj> fetch_service_cat();

    @GET("api/cloth/fetchCloth.php")
    Call<ClothGetObj> fetch_cloths();

    @GET("api/banner/fetchBanners.php")
    Call<BannerGetObj> fetch_banner();


    @POST("api/wallet/fetchwallet.php")
    Call<fetchWallet> fetch_wallet(@Body OnlyIDRequest onlyIDRequest);


    @POST("api/transaction/fetchtransactions.php")
    Call<transactHead> fetch_transaction(@Body OnlyIDRequest onlyIDRequest);


    @POST("api/order/fetchuserorders.php")
    Call<OrderStatusHead> fetch_order_status(@Body OnlyIDRequest userId);

    @POST("api/order/ordersbystatus.php")
    Call<OrderStatusHead> fetch_orderBystatus(@Body fetchByStatus fetch);


    @POST("api/wallet/debitwallet.php")
    Call<Message> debit_wallet(@Body OnlyIDAmountRequest onlyIDAmountRequest);


    @POST("api/wallet/creditwallet.php")
    Call<Message> credit_wallet(@Body OnlyIDAmountRequest onlyIDAmountRequest);



    @POST("api/transaction/newtransaction.php")
    Call<Message> create_transact(@Body newTransact transact);


    @POST("api/transaction/verifytransaction.php")
    Call<Message> verify_transact(@Body VerifyTransact  verifyTransact);

    @POST("api/order/neworder.php")
    Call<Message> new_order(@Body OrderHead orderHead);



    @POST("api/order/updateorderstatus.php")
    Call<Message> updateOrderby_customer(@Body updateOrder order);





}
