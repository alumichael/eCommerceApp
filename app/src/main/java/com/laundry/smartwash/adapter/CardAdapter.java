package com.laundry.smartwash.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.Category.CategoryGetData;

import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.service_activities.serviceForm;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context context;
    private List<CategoryGetData> serviceList;
    String price;


    public CardAdapter(Context context, List<CategoryGetData> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        CategoryGetData serviceOption = serviceList.get(i);

        holder.mProductName.setText(serviceOption.getCateName());
        holder.mDsc.setText(serviceOption.getCateDesc());

        price= "₦" + serviceOption.getCatePrice();
        holder.mProductAmount.setText(price);

        holder.setItemClickListener(pos -> {
                    nextActivity(serviceList.get(pos).getCateName(),serviceList.get(pos).getCateMinPrice(),serviceList.get(pos).getCatePrice(), serviceForm.class);

        });
    }

    private void nextActivity(String cate_name,String min_price,String cate_price, Class productActivityClass) {
        Intent i = new Intent(context, productActivityClass);
        i.putExtra(Constant.CATE_NAME, cate_name);
        i.putExtra(Constant.CATE_MIN_PRICE, min_price);
        i.putExtra(Constant.CATE_PRICE, cate_price);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /** ButterKnife Code **/
        @BindView(R.id.product_layout)
        LinearLayout mProductLayout;
        @BindView(R.id.quote_buy_img)
        ImageView mQuoteBuyImg;
        @BindView(R.id.product_name)
        TextView mProductName;
        @BindView(R.id.dsc)
        TextView mDsc;
        @BindView(R.id.product_amount)
        TextView mProductAmount;
        /** ButterKnife Code **/

        ItemClickListener itemClickListener;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {

            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

    }
}

