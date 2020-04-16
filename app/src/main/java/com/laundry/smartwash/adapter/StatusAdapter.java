package com.laundry.smartwash.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.laundry.smartwash.Constant;
import com.laundry.smartwash.Model.OrderStatusGet.OrderList;
import com.laundry.smartwash.Model.OrderStatusGet.OrderStatusData;
import com.laundry.smartwash.R;
import com.laundry.smartwash.UserMain_Fragment.AcknowledgeActivity;
import com.laundry.smartwash.retrofit_interface.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {

    private Context context;
    LinearLayoutManager layoutManager;
    private List<OrderStatusData> OrderStatusList;
    OrderClothsAdapter orderAdapter;


    public StatusAdapter(Context context, List<OrderStatusData> OrderStatusList) {
        this.context = context;
        this.OrderStatusList = OrderStatusList;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_list, parent, false);
        ButterKnife.bind(this, view);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        OrderStatusData transact = OrderStatusList.get(i);

        holder.mStatusCategory.setText(transact.getCategory());
        String amount_text="Total Amount:NGN "+transact.getTotalAmount();
        holder.mTransactAmount.setText(amount_text);

        if(transact.getStatus().equals("pending")){
            holder.mFlagStatus.setBackgroundColor(Color.RED);
            holder.mFlagStatus.setText("Pending");

        }else if(transact.getStatus().equals("delivered")){

            holder.mFlagStatus.setText("Delivered");

        }else{
            holder.mFlagStatus.setText("Received");
        }

        layoutManager = new LinearLayoutManager(context);
        holder.mRecyclerViewCloths.setLayoutManager(layoutManager);

        orderAdapter = new OrderClothsAdapter(context,transact.getOrders());
        holder.mRecyclerViewCloths.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();


        holder.mRecyclerViewCloths.setVisibility(View.GONE);
        holder.mCountNothing.setVisibility(View.GONE);
        holder.mCountMore.setVisibility(View.VISIBLE);

        holder.mCountMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mRecyclerViewCloths.setVisibility(View.VISIBLE);
                holder.mCountNothing.setVisibility(View.VISIBLE);
                holder.mCountMore.setVisibility(View.GONE);

            }
        });

        holder.mCountNothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mRecyclerViewCloths.setVisibility(View.GONE);
                holder.mCountNothing.setVisibility(View.GONE);
                holder.mCountMore.setVisibility(View.VISIBLE);

            }
        });

        if(transact.getStatus().equals("delivered")){

            holder.setItemClickListener(pos -> {

                nextActivity(OrderStatusList.get(pos).getOrderID(),OrderStatusList.get(pos).getUserID(),"Received",
                        AcknowledgeActivity.class);

            });

        }else{
            holder.setItemClickListener(pos -> {



            });
        }



    }

    private void nextActivity(String orderId,String userId,String receivedStatus, Class productActivityClass) {
        Intent i = new Intent(context, productActivityClass);
        i.putExtra(Constant.ORDER_ID, orderId);
        i.putExtra(Constant.CUSTOMER_ID, userId);
        i.putExtra(Constant.ACKNWOLEDGE, receivedStatus);
        context.startActivity(i);
    }


    @Override
    public int getItemCount() {
        return OrderStatusList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.status_list_layout)
        LinearLayout mStatusListLayout;
        @BindView(R.id.status_category)
        TextView mStatusCategory;
        @BindView(R.id.transact_amount)
        TextView mTransactAmount;
        @BindView(R.id.status_flag)
        TextView mFlagStatus;

        @BindView(R.id.show_more)
        ImageButton mCountMore;
        @BindView(R.id.show_nothing)
        ImageButton mCountNothing;

        @BindView(R.id.recycler_view_cloths)
        RecyclerView mRecyclerViewCloths;



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

